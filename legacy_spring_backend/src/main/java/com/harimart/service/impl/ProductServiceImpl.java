package com.harimart.service.impl;

import com.harimart.dto.ProductDTO;
import com.harimart.dto.ProductFilterRequest;
import com.harimart.entity.Category;
import com.harimart.entity.Product;
import com.harimart.entity.ProductImage;
import com.harimart.exception.ResourceNotFoundException;
import com.harimart.repository.CategoryRepository;
import com.harimart.repository.ProductImageRepository;
import com.harimart.repository.ProductRepository;
import com.harimart.repository.ProductSpecification;
import com.harimart.repository.ReviewRepository;
import com.harimart.service.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductImageRepository productImageRepository;
    private final ReviewRepository reviewRepository;

    public ProductServiceImpl(ProductRepository productRepository,
                              CategoryRepository categoryRepository,
                              ProductImageRepository productImageRepository,
                              ReviewRepository reviewRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.productImageRepository = productImageRepository;
        this.reviewRepository = reviewRepository;
    }

    @Override
    public Page<ProductDTO> getProducts(ProductFilterRequest filter) {
        Sort sort = createSort(filter.getSort());
        Pageable pageable = PageRequest.of(filter.getPage(), filter.getSize(), sort);

        Specification<Product> spec = ProductSpecification.filter(filter);
        Page<Product> productPage = productRepository.findAll(spec, pageable);

        List<ProductDTO> dtos = productPage.getContent().stream()
                .map(this::mapToDTO)
                .filter(dto -> {
                    if (filter.getRating() != null && filter.getRating() > 0) {
                        return dto.getRating() >= filter.getRating();
                    }
                    return true;
                })
                .collect(Collectors.toList());

        return new PageImpl<>(dtos, pageable, productPage.getTotalElements());
    }

    @Override
    public ProductDTO getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        return mapToDTO(product);
    }

    @Override
    public List<ProductDTO> getFeaturedProducts(int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        return productRepository.findFeaturedProducts(pageable).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductDTO> getTrendingProducts(int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        return productRepository.findTrendingProducts(pageable).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductDTO> getBestSellers(int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        return productRepository.findBestSellers(pageable).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductDTO> getRelatedProducts(Long productId, int limit) {
        Product current = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        return productRepository.findByCategoryId(current.getCategory().getId()).stream()
                .filter(p -> !p.getId().equals(productId) && "ACTIVE".equalsIgnoreCase(p.getStatus()))
                .limit(limit)
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ProductDTO createProduct(ProductDTO dto) {
        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + dto.getCategoryId()));

        Product product = new Product();
        product.setName(dto.getName().trim());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());
        product.setDiscount(dto.getDiscount() != null ? dto.getDiscount() : BigDecimal.ZERO);
        product.setStock(dto.getStock() != null ? dto.getStock() : 0);
        product.setBrand(dto.getBrand().trim());
        product.setCategory(category);
        product.setImageUrl(dto.getImageUrl());
        product.setStatus(dto.getStatus() != null ? dto.getStatus() : "ACTIVE");

        Product saved = productRepository.save(product);

        // Save additional images if provided
        if (dto.getImages() != null && !dto.getImages().isEmpty()) {
            for (String imgUrl : dto.getImages()) {
                if (imgUrl != null && !imgUrl.trim().isEmpty()) {
                    ProductImage img = new ProductImage();
                    img.setProduct(saved);
                    img.setImageUrl(imgUrl.trim());
                    productImageRepository.save(img);
                }
            }
        }

        return mapToDTO(saved);
    }

    @Override
    @Transactional
    public ProductDTO updateProduct(Long id, ProductDTO dto) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));

        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + dto.getCategoryId()));

        product.setName(dto.getName().trim());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());
        product.setDiscount(dto.getDiscount() != null ? dto.getDiscount() : BigDecimal.ZERO);
        product.setStock(dto.getStock() != null ? dto.getStock() : 0);
        product.setBrand(dto.getBrand().trim());
        product.setCategory(category);
        product.setImageUrl(dto.getImageUrl());
        if (dto.getStatus() != null) {
            product.setStatus(dto.getStatus());
        }

        Product updated = productRepository.save(product);

        // Update gallery images if provided
        if (dto.getImages() != null) {
            productImageRepository.deleteByProductId(id);
            for (String imgUrl : dto.getImages()) {
                if (imgUrl != null && !imgUrl.trim().isEmpty()) {
                    ProductImage img = new ProductImage();
                    img.setProduct(updated);
                    img.setImageUrl(imgUrl.trim());
                    productImageRepository.save(img);
                }
            }
        }

        return mapToDTO(updated);
    }

    @Override
    @Transactional
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        // Soft delete status for catalog or hard delete
        product.setStatus("INACTIVE");
        productRepository.save(product);
    }

    private Sort createSort(String sortParam) {
        if (sortParam == null) {
            return Sort.by(Sort.Direction.DESC, "createdAt");
        }
        return switch (sortParam.toLowerCase()) {
            case "priceasc", "price_asc", "low_to_high" -> Sort.by(Sort.Direction.ASC, "price");
            case "pricedesc", "price_desc", "high_to_low" -> Sort.by(Sort.Direction.DESC, "price");
            case "rating" -> Sort.by(Sort.Direction.DESC, "id");
            case "newest" -> Sort.by(Sort.Direction.DESC, "createdAt");
            default -> Sort.by(Sort.Direction.DESC, "createdAt");
        };
    }

    private ProductDTO mapToDTO(Product product) {
        ProductDTO dto = new ProductDTO();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setDescription(product.getDescription());
        dto.setPrice(product.getPrice());
        dto.setDiscount(product.getDiscount());
        dto.setFinalPrice(product.getFinalPrice());
        dto.setStock(product.getStock());
        dto.setBrand(product.getBrand());
        dto.setCategoryId(product.getCategory().getId());
        dto.setCategoryName(product.getCategory().getName());
        dto.setImageUrl(product.getImageUrl());
        dto.setStatus(product.getStatus());
        dto.setVersion(product.getVersion());

        // Multi-images
        List<ProductImage> gallery = productImageRepository.findByProductId(product.getId());
        List<String> imageList = new ArrayList<>();
        if (product.getImageUrl() != null && !product.getImageUrl().isEmpty()) {
            imageList.add(product.getImageUrl());
        }
        for (ProductImage img : gallery) {
            if (!imageList.contains(img.getImageUrl())) {
                imageList.add(img.getImageUrl());
            }
        }
        dto.setImages(imageList);

        // Ratings & review count
        Double avgRating = reviewRepository.getAverageRatingForProduct(product.getId());
        long reviewCount = reviewRepository.countByProductId(product.getId());
        dto.setRating(avgRating != null ? Math.round(avgRating * 10.0) / 10.0 : 4.5); // Default realistic 4.5 rating if no reviews yet
        dto.setReviewCount((int) reviewCount);

        return dto;
    }
}
