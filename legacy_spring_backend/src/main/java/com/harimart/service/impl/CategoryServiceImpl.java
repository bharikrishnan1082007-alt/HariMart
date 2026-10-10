package com.harimart.service.impl;

import com.harimart.dto.CategoryDTO;
import com.harimart.entity.Category;
import com.harimart.exception.BadRequestException;
import com.harimart.exception.ResourceNotFoundException;
import com.harimart.repository.CategoryRepository;
import com.harimart.repository.ProductRepository;
import com.harimart.service.CategoryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    public CategoryServiceImpl(CategoryRepository categoryRepository, ProductRepository productRepository) {
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
    }

    @Override
    public List<CategoryDTO> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public CategoryDTO getCategoryById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));
        return mapToDTO(category);
    }

    @Override
    @Transactional
    public CategoryDTO createCategory(CategoryDTO categoryDTO) {
        if (categoryRepository.existsByNameIgnoreCase(categoryDTO.getName().trim())) {
            throw new BadRequestException("Category with name '" + categoryDTO.getName() + "' already exists");
        }

        Category category = new Category();
        category.setName(categoryDTO.getName().trim());
        category.setDescription(categoryDTO.getDescription());
        category.setImage(categoryDTO.getImage());
        category.setStatus(categoryDTO.getStatus() != null ? categoryDTO.getStatus() : "ACTIVE");

        Category saved = categoryRepository.save(category);
        return mapToDTO(saved);
    }

    @Override
    @Transactional
    public CategoryDTO updateCategory(Long id, CategoryDTO categoryDTO) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));

        if (categoryRepository.existsByNameIgnoreCaseAndIdNot(categoryDTO.getName().trim(), id)) {
            throw new BadRequestException("Category with name '" + categoryDTO.getName() + "' already exists");
        }

        category.setName(categoryDTO.getName().trim());
        category.setDescription(categoryDTO.getDescription());
        category.setImage(categoryDTO.getImage());
        if (categoryDTO.getStatus() != null) {
            category.setStatus(categoryDTO.getStatus());
        }

        Category updated = categoryRepository.save(category);
        return mapToDTO(updated);
    }

    @Override
    @Transactional
    public void deleteCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));

        long count = productRepository.countByCategoryId(id);
        if (count > 0) {
            throw new BadRequestException("Cannot delete category with " + count + " associated products. Reassign or remove products first.");
        }

        categoryRepository.delete(category);
    }

    private CategoryDTO mapToDTO(Category category) {
        CategoryDTO dto = new CategoryDTO(
                category.getId(),
                category.getName(),
                category.getDescription(),
                category.getImage(),
                category.getStatus()
        );
        dto.setProductCount(productRepository.countByCategoryId(category.getId()));
        return dto;
    }
}
