package com.harimart.dto;

import jakarta.validation.constraints.NotBlank;

public class CategoryDTO {

    private Long id;

    @NotBlank(message = "Category name is required")
    private String name;

    private String description;
    private String image;
    private String status;
    private Long productCount;

    public CategoryDTO() {
    }

    public CategoryDTO(Long id, String name, String description, String image, String status) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.image = image;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getProductCount() {
        return productCount;
    }

    public void setProductCount(Long productCount) {
        this.productCount = productCount;
    }
}
