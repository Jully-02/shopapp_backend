package com.shopapp.services;

import com.shopapp.dtos.ProductDTO;
import com.shopapp.dtos.ProductImageDTO;
import com.shopapp.exceptions.DataNotFoundException;
import com.shopapp.exceptions.InvalidParamException;
import com.shopapp.models.Category;
import com.shopapp.models.Product;
import com.shopapp.models.ProductImage;
import com.shopapp.repositories.CategoryRepository;
import com.shopapp.repositories.ProductImageRepository;
import com.shopapp.repositories.ProductRepository;
import com.shopapp.responses.ProductResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@RequiredArgsConstructor
@Service
public class ProductService implements IProductService{

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductImageRepository productImageRepository;
    @Override
    @Transactional
    public Product createProduct(ProductDTO productDTO) throws DataNotFoundException {
        Category existingCategory = categoryRepository.findById(productDTO.getCategoryId())
                .orElseThrow(() ->
                        new DataNotFoundException("Cannot find Category with ID = " + productDTO.getCategoryId() ));
        Product newProduct = Product.builder()
                .title(productDTO.getTitle())
                .price(productDTO.getPrice())
                .thumbnail(productDTO.getThumbnail())
                .description(productDTO.getDescription())
                .category(existingCategory)
                .build();
        productRepository.save(newProduct);
        return newProduct;
    }

    @Override
    public Product getProductById(Long id) throws DataNotFoundException {
        return productRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Cannot find Product with ID = " + id));
    }

    @Override
    public List<Product> findProductByIds (List<Long> productIds) {
        return productRepository.findProductByIds(productIds);
    }

    @Override
    public Page<ProductResponse> getAllProducts(
            String keyword,
            Long categoryId,
            PageRequest pageRequest
    ) {
        // Get a list of products by page and limit
        Page<Product> productPage;
        productPage = productRepository.searchProducts(categoryId, keyword, pageRequest);
        return productPage.map(ProductResponse::fromProduct);
    }

    @Override
    @Transactional
    public Product updateProduct(
            Long id,
            ProductDTO productDTO
    ) throws DataNotFoundException {
        Product existingProduct = getProductById(id);
        if (existingProduct != null) {
            // Copy data from DTO -> product
            // Can be use ModelMapper or ObjectMapper
            Category existingCategory = categoryRepository.findById(productDTO.getCategoryId())
                            .orElseThrow(() -> new DataNotFoundException("Cannot find Category with ID = " + productDTO.getCategoryId()));
            existingProduct.setTitle(productDTO.getTitle());
            existingProduct.setPrice(productDTO.getPrice());
            existingProduct.setCategory(existingCategory);
            existingProduct.setDescription(productDTO.getDescription());
            existingProduct.setThumbnail(productDTO.getThumbnail());
            return productRepository.save(existingProduct);
        }
        return null;
    }

    @Override
    @Transactional
    public void deleteProduct(Long id) {
        Optional<Product> existingProduct = productRepository.findById(id);
        existingProduct.ifPresent(productRepository::delete);
    }

    @Override
    public boolean existsByName(String title) {
        return productRepository.existsByTitle(title);
    }

    @Override
    @Transactional
    public ProductImage createProductImage (
            Long productId,
            ProductImageDTO productImageDTO
    ) throws Exception {
        Product existingProduct = productRepository.findById(productId)
                .orElseThrow(() -> new DataNotFoundException("Cannot find Product with ID = " + productId));
        ProductImage newProductImage = ProductImage.builder()
                .product(existingProduct)
                .imageUrl(productImageDTO.getImageUrl())
                .build();
        // Do not insert more than 5 images for 1 product
        int size = productImageRepository.findByProductId(productId).size();
        if (size > ProductImage.MAXIMUM_IMAGES_PER_PRODUCT) {
            throw new InvalidParamException(
                    "Number of image must be <= "
                    + ProductImage.MAXIMUM_IMAGES_PER_PRODUCT);
        }
        return productImageRepository.save(newProductImage);
    }
}
