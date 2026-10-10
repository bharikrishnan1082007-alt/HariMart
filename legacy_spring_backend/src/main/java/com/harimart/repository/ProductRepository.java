package com.harimart.repository;

import com.harimart.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {

    List<Product> findByCategoryId(Long categoryId);

    long countByCategoryId(Long categoryId);

    long countByStockLessThanEqualAndStatus(int stockThreshold, String status);

    List<Product> findByStatus(String status);

    // Featured products
    @Query("SELECT p FROM Product p WHERE p.status = 'ACTIVE' ORDER BY p.discount DESC")
    List<Product> findFeaturedProducts(Pageable pageable);

    // Trending products
    @Query("SELECT p FROM Product p WHERE p.status = 'ACTIVE' ORDER BY p.createdAt DESC")
    List<Product> findTrendingProducts(Pageable pageable);

    // Best-selling products (sorted by stock or sales)
    @Query("SELECT p FROM Product p WHERE p.status = 'ACTIVE' ORDER BY p.id ASC")
    List<Product> findBestSellers(Pageable pageable);

    // Search by keyword in name or brand
    Page<Product> findByNameContainingIgnoreCaseOrBrandContainingIgnoreCase(String name, String brand, Pageable pageable);
}
