package com.harimart.service.impl;

import com.harimart.dto.DashboardStatsDTO;
import com.harimart.dto.OrderDTO;
import com.harimart.dto.OrderItemDTO;
import com.harimart.entity.Order;
import com.harimart.entity.OrderItem;
import com.harimart.enums.OrderStatus;
import com.harimart.repository.OrderItemRepository;
import com.harimart.repository.OrderRepository;
import com.harimart.repository.ProductRepository;
import com.harimart.repository.UserRepository;
import com.harimart.service.DashboardService;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DashboardServiceImpl implements DashboardService {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    public DashboardServiceImpl(UserRepository userRepository,
                                ProductRepository productRepository,
                                OrderRepository orderRepository,
                                OrderItemRepository orderItemRepository) {
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
    }

    @Override
    public DashboardStatsDTO getDashboardStats() {
        DashboardStatsDTO stats = new DashboardStatsDTO();

        stats.setTotalUsers(userRepository.count());
        stats.setTotalProducts(productRepository.count());
        stats.setTotalOrders(orderRepository.count());

        BigDecimal revenue = orderRepository.calculateTotalRevenue();
        stats.setTotalRevenue(revenue != null ? revenue : BigDecimal.ZERO);

        stats.setPendingOrders(orderRepository.countByOrderStatus(OrderStatus.PLACED));
        stats.setLowStockProducts(productRepository.countByStockLessThanEqualAndStatus(5, "ACTIVE"));

        List<Order> recentOrders = orderRepository.findRecentOrders(PageRequest.of(0, 10));
        List<OrderDTO> recentOrderDTOs = recentOrders.stream().map(this::mapOrderToDTO).collect(Collectors.toList());
        stats.setRecentOrders(recentOrderDTOs);

        return stats;
    }

    private OrderDTO mapOrderToDTO(Order order) {
        OrderDTO dto = new OrderDTO();
        dto.setId(order.getId());
        dto.setOrderNumber(order.getOrderNumber());
        dto.setUserId(order.getUser().getId());
        dto.setCustomerName(order.getUser().getName());
        dto.setCustomerEmail(order.getUser().getEmail());
        dto.setCustomerPhone(order.getUser().getPhone());
        dto.setTotalAmount(order.getTotalAmount());
        dto.setDiscount(order.getDiscount());
        dto.setDeliveryCharge(order.getDeliveryCharge());
        dto.setFinalAmount(order.getFinalAmount());
        dto.setPaymentMethod(order.getPaymentMethod());
        dto.setPaymentStatus(order.getPaymentStatus());
        dto.setOrderStatus(order.getOrderStatus());
        dto.setShippingAddress(order.getShippingAddress());
        dto.setCreatedAt(order.getCreatedAt());
        dto.setUpdatedAt(order.getUpdatedAt());

        List<OrderItem> items = orderItemRepository.findByOrderId(order.getId());
        List<OrderItemDTO> itemDTOs = items.stream().map(i -> {
            BigDecimal itemTotal = i.getPrice().multiply(BigDecimal.valueOf(i.getQuantity()));
            return new OrderItemDTO(
                    i.getId(),
                    i.getProduct().getId(),
                    i.getProduct().getName(),
                    i.getProduct().getImageUrl(),
                    i.getProduct().getBrand(),
                    i.getQuantity(),
                    i.getPrice(),
                    itemTotal
            );
        }).collect(Collectors.toList());

        dto.setItems(itemDTOs);
        return dto;
    }
}
