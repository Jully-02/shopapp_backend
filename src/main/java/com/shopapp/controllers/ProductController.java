package com.shopapp.controllers;

import com.github.javafaker.Faker;
import com.shopapp.components.LocalizationUtils;
import com.shopapp.dtos.ProductDTO;
import com.shopapp.dtos.ProductImageDTO;
import com.shopapp.models.Product;
import com.shopapp.models.ProductImage;
import com.shopapp.responses.ProductListResponse;
import com.shopapp.responses.ProductResponse;
import com.shopapp.services.IProductService;
import com.shopapp.utils.MessageKeys;
import jakarta.annotation.security.PermitAll;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("${api.prefix}/products")
@RequiredArgsConstructor
public class ProductController {
    private final IProductService productService;
    private final LocalizationUtils localizationUtils;
    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);

    private boolean isImageFile (MultipartFile file) {
        String contentType = file.getContentType();
        return contentType != null && contentType.startsWith("image/");
    }
    private String storeFile (MultipartFile file) throws IOException {
        if (!isImageFile(file) || file.getOriginalFilename() == null) {
            throw new IOException("Invalid image format");
        }
        String filename = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        // Add UUID before old filename to make it unique
        String uniqueFilename = UUID.randomUUID().toString() + "_" + filename;
        // Path to the folder where you want to save the file
        Path uploadDir = Paths.get("uploads");
        if(!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);
        }
        // Full path to the file
        Path destination = Paths.get(uploadDir.toString(), uniqueFilename);
        // Copy files to the destination folder
        Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);
        return uniqueFilename;
    }
    @GetMapping("")
    public ResponseEntity<ProductListResponse> getProducts (
            @RequestParam(defaultValue = "0") int page, // Pages start from 0
            @RequestParam(defaultValue = "10")  int limit,
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(defaultValue = "0", name = "category_id") Long categoryId
    ) {
        // Create Pageable from page information and bounds
        PageRequest pageRequest = PageRequest.of(
                page, limit,
                //Sort.by("createdAt").descending()
                Sort.by("id").ascending());
        logger.info(String.format("keyword = %s, category_id = %d, page = %d, limit = %d", keyword, categoryId, page,limit));
        Page<ProductResponse> productPage = productService.getAllProducts(keyword, categoryId, pageRequest);
        // Get the total number of pages
        int totalPages = productPage.getTotalPages();
        List<ProductResponse> products = productPage.getContent();
        return ResponseEntity.ok(ProductListResponse.builder()
                .products(products)
                .totalPages(totalPages)
                .build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getProductById (@PathVariable("id") Long productId) {
        try {
            Product existingProduct = productService.getProductById(productId);
            return ResponseEntity.ok(ProductResponse.fromProduct(existingProduct));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/by-ids")
    public ResponseEntity<?> getProductByIds(@RequestParam("ids") String ids) {
        //eg: 1,3,5,7
        // Tách chuỗi ids thành một mảng các số nguyên
        try {
            List<Long> productIds = Arrays.stream(ids.split(","))
                    .map(Long::parseLong)
                    .collect(Collectors.toList());
            List<Product> products = productService.findProductByIds(productIds);
            return ResponseEntity.ok(products);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("")
    public ResponseEntity<?> createProduct (
            @Valid @RequestBody ProductDTO productDTO,
            BindingResult result
    ) {
        try {
            if (result.hasErrors()) {
                List<String> errorMessages = result.getFieldErrors()
                        .stream()
                        .map(FieldError::getDefaultMessage)
                        .toList();
                return ResponseEntity.badRequest().body(errorMessages);
            }
            Product newProduct = productService.createProduct(productDTO);
            return ResponseEntity.ok(newProduct);
        } catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping(value = "uploads/{id}",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadImages (
            @PathVariable("id") Long productId,
            @ModelAttribute("file") List<MultipartFile> files
    ) {
        try {
            Product existingProduct = productService.getProductById(productId);
            files = files == null ? new ArrayList<MultipartFile>() : files;
            if (files.size() > ProductImage.MAXIMUM_IMAGES_PER_PRODUCT) {
                return ResponseEntity.badRequest().body(localizationUtils.getLocalizedMessage(MessageKeys.UPLOAD_IMAGE_MAX_5));
            }
            List<ProductImage> productImages = new ArrayList<>();
            for(MultipartFile file : files) {
                if (file.getSize() == 0) {
                    continue;
                }
                if(file.getSize() > 10 * 1024 * 1024) { // Size of file > 10MB
                    return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE)
                            .body(localizationUtils.getLocalizedMessage(MessageKeys.UPLOAD_IMAGES_FILE_LARGE));
                }
                String contentType = file.getContentType();
                if (contentType == null || !contentType.startsWith("image/")) {
                    return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
                            .body(localizationUtils.getLocalizedMessage(MessageKeys.UPLOAD_IMAGES_FILE_MUST_BE_IMAGE));
                }
                // Save file and update thumbnail in DTO
                String filename = storeFile(file); // Replace this function with your code to save the file
                // Save to product object in DB => Do next
                ProductImage productImage = productService.createProductImage(
                        existingProduct.getId(),
                        ProductImageDTO.builder()
                                .imageUrl(filename)
                                .build()
                );
                productImages.add(productImage);
            }
            return ResponseEntity.ok().body(productImages);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/images/{imageName}")
    public ResponseEntity<?> viewImage (@PathVariable String imageName) {
        try {
            Path imagePath = Paths.get("uploads/"+imageName);
            UrlResource resource = new UrlResource(imagePath.toUri());

            if (resource.exists()) {
                return ResponseEntity.ok()
                        .contentType(MediaType.IMAGE_JPEG)
                        .body(resource);
            } else {
                return ResponseEntity.ok()
                        .contentType(MediaType.IMAGE_PNG)
                        .body(new UrlResource(Paths.get("uploads/notfound.png").toUri()));
                // return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updatedProduct (
            @PathVariable Long id,
            @RequestBody ProductDTO productDTO
    ) {
       try {
           Product updatedProduct = productService.updateProduct(id, productDTO);
           return ResponseEntity.ok(updatedProduct);
       } catch (Exception e) {
           return ResponseEntity.badRequest().body(e.getMessage());
       }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteProduct (@PathVariable Long id) {
        try {
            productService.deleteProduct(id);
            return ResponseEntity.ok(localizationUtils.getLocalizedMessage(MessageKeys.DELETE_PRODUCT_SUCCESSFULLY, id));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

//    @PostMapping("/generateFakeProducts")
    private ResponseEntity<String> generateFakeProducts () {
        Faker faker = new Faker();
        for (int i = 0; i < 1_000_000; i++) {
            String title = faker.commerce().productName();
            if(productService.existsByName(title)) {
                continue;
            }
            ProductDTO productDTO = ProductDTO.builder()
                    .title(title)
                    .price((float) faker.number().numberBetween(100,90_000_00))
                    .description(faker.lorem().sentence())
                    .thumbnail("")
                    .categoryId((long)faker.number().numberBetween(2,6))
                    .build();
            try {
                productService.createProduct(productDTO);
            } catch (Exception e) {
                return ResponseEntity.badRequest().body(e.getMessage());
            }
        }
        return ResponseEntity.ok("Fake products created successfully");
    }
}
