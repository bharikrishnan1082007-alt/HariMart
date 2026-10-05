package com.harimart;

import com.harimart.dto.CategoryDTO;
import com.harimart.dto.ProductDTO;
import com.harimart.dto.ProductFilterRequest;
import com.harimart.service.CategoryService;
import com.harimart.service.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("dev")
@Transactional
class ProductServiceTest {

    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryService categoryService;

    @Test
    void testGetProductsPaginationAndFiltering() {
        ProductFilterRequest filter = new ProductFilterRequest();
        filter.setPage(0);
        filter.setSize(10);

        Page<ProductDTO> result = productService.getProducts(filter);
        assertNotNull(result);
        assertTrue(result.getTotalElements() > 0);
    }

    @Test
    void testSearchProductByKeyword() {
        ProductFilterRequest filter = new ProductFilterRequest();
        filter.setSearch("Headphones");

        Page<ProductDTO> result = productService.getProducts(filter);
        assertNotNull(result);
        assertTrue(result.getContent().stream().anyMatch(p -> p.getName().contains("Headphones")));
    }

    @Test
    void testCreateAndGetProduct() {
        List<CategoryDTO> categories = categoryService.getAllCategories();
        assertFalse(categories.isEmpty());

        ProductDTO dto = new ProductDTO();
        dto.setName("Unit Test Wireless Mouse");
        dto.setDescription("Ergonomic 2.4GHz wireless optical mouse");
        dto.setPrice(new BigDecimal("799.00"));
        dto.setDiscount(new BigDecimal("10.00"));
        dto.setStock(50);
        dto.setBrand("LogiClick");
        dto.setCategoryId(categories.get(0).getId());
        dto.setImageUrl("https://example.com/mouse.jpg");
        dto.setStatus("ACTIVE");

        ProductDTO created = productService.createProduct(dto);
        assertNotNull(created.getId());
        assertEquals("Unit Test Wireless Mouse", created.getName());
        assertEquals(new BigDecimal("719.10"), created.getFinalPrice()); // 799 - 10% = 719.10
    }
}
