package com.harimart.service;

import com.harimart.dto.ProductDTO;
import com.harimart.dto.ProductFilterRequest;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ProductService {

    Page<ProductDTO> getProducts(ProductFilterRequest filter);

    ProductDTO getProductById(Long id);

    List<ProductDTO> getFeaturedProducts(int limit);

    List<ProductDTO> getTrendingProducts(int limit);

    List<ProductDTO> getBestSellers(int limit);

    List<ProductDTO> getRelatedProducts(Long productId, int limit);

    ProductDTO createProduct(ProductDTO productDTO);

    ProductDTO updateProduct(Long id, ProductDTO productDTO);

    void deleteProduct(Long id);
}
