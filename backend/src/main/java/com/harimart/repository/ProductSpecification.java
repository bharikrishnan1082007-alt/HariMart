package com.harimart.repository;

import com.harimart.dto.ProductFilterRequest;
import com.harimart.entity.Product;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class ProductSpecification {

    public static Specification<Product> filter(ProductFilterRequest filter) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Status must be ACTIVE for public catalog
            predicates.add(cb.equal(root.get("status"), "ACTIVE"));

            // Search by name, brand, or description
            if (filter.getSearch() != null && !filter.getSearch().trim().isEmpty()) {
                String term = "%" + filter.getSearch().trim().toLowerCase() + "%";
                Predicate nameMatch = cb.like(cb.lower(root.get("name")), term);
                Predicate brandMatch = cb.like(cb.lower(root.get("brand")), term);
                Predicate descMatch = cb.like(cb.lower(root.get("description")), term);
                predicates.add(cb.or(nameMatch, brandMatch, descMatch));
            }

            // Category filter by name
            if (filter.getCategory() != null && !filter.getCategory().trim().isEmpty()) {
                predicates.add(cb.equal(cb.lower(root.get("category").get("name")), filter.getCategory().trim().toLowerCase()));
            }

            // Brand filter
            if (filter.getBrand() != null && !filter.getBrand().trim().isEmpty()) {
                predicates.add(cb.equal(cb.lower(root.get("brand")), filter.getBrand().trim().toLowerCase()));
            }

            // Min Price
            if (filter.getMinPrice() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("price"), filter.getMinPrice()));
            }

            // Max Price
            if (filter.getMaxPrice() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("price"), filter.getMaxPrice()));
            }

            // In Stock only
            if (filter.getInStock() != null && filter.getInStock()) {
                predicates.add(cb.greaterThan(root.get("stock"), 0));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
