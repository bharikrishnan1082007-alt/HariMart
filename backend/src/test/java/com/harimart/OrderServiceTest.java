package com.harimart;

import com.harimart.dto.OrderDTO;
import com.harimart.dto.OrderRequest;
import com.harimart.dto.ProductDTO;
import com.harimart.dto.ProductFilterRequest;
import com.harimart.enums.OrderStatus;
import com.harimart.enums.PaymentMethod;
import com.harimart.exception.BadRequestException;
import com.harimart.repository.ProductRepository;
import com.harimart.service.OrderService;
import com.harimart.service.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("dev")
@Transactional
class OrderServiceTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;

    @Test
    @WithMockUser(username = "customer@harimart.com", authorities = "ROLE_CUSTOMER")
    void testDirectBuyNowOrderPlacementWithCouponAndStockDeduction() {
        ProductFilterRequest filter = new ProductFilterRequest();
        filter.setSize(1);
        List<ProductDTO> products = productService.getProducts(filter).getContent();
        assertFalse(products.isEmpty());

        ProductDTO product = products.get(0);
        int initialStock = product.getStock();

        OrderRequest orderReq = new OrderRequest();
        orderReq.setDirectProductId(product.getId());
        orderReq.setDirectQuantity(2);
        orderReq.setPaymentMethod(PaymentMethod.ONLINE_MOCK);
        orderReq.setCouponCode("HARI10");

        OrderDTO placed = orderService.placeOrder(orderReq);

        assertNotNull(placed.getOrderNumber());
        assertEquals(OrderStatus.PLACED, placed.getOrderStatus());
        assertTrue(placed.getDiscount().compareTo(BigDecimal.ZERO) > 0);

        // Verify stock deduction
        ProductDTO updatedProduct = productService.getProductById(product.getId());
        assertEquals(initialStock - 2, updatedProduct.getStock());

        // Test cancellation while PLACED
        OrderDTO cancelled = orderService.cancelOrder(placed.getId());
        assertEquals(OrderStatus.CANCELLED, cancelled.getOrderStatus());

        // Verify stock restoration
        ProductDTO restoredProduct = productService.getProductById(product.getId());
        assertEquals(initialStock, restoredProduct.getStock());
    }

    @Test
    @WithMockUser(username = "customer@harimart.com", authorities = "ROLE_CUSTOMER")
    void testOversellingPreventionThrowsBadRequest() {
        ProductFilterRequest filter = new ProductFilterRequest();
        filter.setSize(1);
        ProductDTO product = productService.getProducts(filter).getContent().get(0);

        OrderRequest orderReq = new OrderRequest();
        orderReq.setDirectProductId(product.getId());
        orderReq.setDirectQuantity(product.getStock() + 100); // Exceeds stock
        orderReq.setPaymentMethod(PaymentMethod.COD);

        assertThrows(BadRequestException.class, () -> orderService.placeOrder(orderReq));
    }
}
