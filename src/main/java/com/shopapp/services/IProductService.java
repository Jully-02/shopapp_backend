package com.shopapp.services;

import com.shopapp.dtos.ProductDTO;
import com.shopapp.dtos.ProductImageDTO;
import com.shopapp.exceptions.DataNotFoundException;
import com.shopapp.models.Product;
import com.shopapp.models.ProductImage;
import com.shopapp.responses.ProductResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

public interface IProductService {
    Product createProduct (ProductDTO productDTO) throws Exception;

    Product getProductById (Long id) throws Exception;

    List<Product> findProductByIds (List<Long> productIds);

    Page<ProductResponse> getAllProducts (
            String keyword,
            Long categoryId,
            PageRequest pageRequest);

    Product updateProduct (Long id, ProductDTO productDTO) throws Exception;

    void deleteProduct(Long id);

    boolean existsByName (String title);

     ProductImage createProductImage (
            Long id,
            ProductImageDTO productImageDTO
    ) throws Exception;
}
