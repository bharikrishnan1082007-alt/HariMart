package com.harimart.controller;

import com.harimart.dto.ApiResponse;
import com.harimart.dto.ProductDTO;
import com.harimart.dto.ProductFilterRequest;
import com.harimart.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<ProductDTO>>> getProducts(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) Double rating,
            @RequestParam(required = false) Boolean inStock,
            @RequestParam(defaultValue = "newest") String sort,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        ProductFilterRequest filter = new ProductFilterRequest();
        filter.setSearch(search);
        filter.setCategory(category);
        filter.setBrand(brand);
        filter.setMinPrice(minPrice);
        filter.setMaxPrice(maxPrice);
        filter.setRating(rating);
        filter.setInStock(inStock);
        filter.setSort(sort);
        filter.setPage(page);
        filter.setSize(size);

        Page<ProductDTO> products = productService.getProducts(filter);
        return ResponseEntity.ok(ApiResponse.ok(products));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductDTO>> getProductById(@PathVariable Long id) {
        ProductDTO product = productService.getProductById(id);
        return ResponseEntity.ok(ApiResponse.ok(product));
    }

    @GetMapping("/featured")
    public ResponseEntity<ApiResponse<List<ProductDTO>>> getFeaturedProducts(@RequestParam(defaultValue = "8") int limit) {
        List<ProductDTO> products = productService.getFeaturedProducts(limit);
        return ResponseEntity.ok(ApiResponse.ok(products));
    }

    @GetMapping("/trending")
    public ResponseEntity<ApiResponse<List<ProductDTO>>> getTrendingProducts(@RequestParam(defaultValue = "8") int limit) {
        List<ProductDTO> products = productService.getTrendingProducts(limit);
        return ResponseEntity.ok(ApiResponse.ok(products));
    }

    @GetMapping("/bestsellers")
    public ResponseEntity<ApiResponse<List<ProductDTO>>> getBestSellers(@RequestParam(defaultValue = "8") int limit) {
        List<ProductDTO> products = productService.getBestSellers(limit);
        return ResponseEntity.ok(ApiResponse.ok(products));
    }

    @GetMapping("/{id}/related")
    public ResponseEntity<ApiResponse<List<ProductDTO>>> getRelatedProducts(
            @PathVariable Long id,
            @RequestParam(defaultValue = "4") int limit) {
        List<ProductDTO> products = productService.getRelatedProducts(id, limit);
        return ResponseEntity.ok(ApiResponse.ok(products));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<ProductDTO>> createProduct(@Valid @RequestBody ProductDTO productDTO) {
        ProductDTO created = productService.createProduct(productDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Product created successfully", created));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<ProductDTO>> updateProduct(@PathVariable Long id, @Valid @RequestBody ProductDTO productDTO) {
        ProductDTO updated = productService.updateProduct(id, productDTO);
        return ResponseEntity.ok(ApiResponse.ok("Product updated successfully", updated));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok(ApiResponse.ok("Product deleted successfully", null));
    }
}
