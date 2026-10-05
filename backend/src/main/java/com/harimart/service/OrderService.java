package com.harimart.service;

import com.harimart.dto.OrderDTO;
import com.harimart.dto.OrderRequest;
import com.harimart.dto.OrderStatusUpdateRequest;

import java.util.List;

public interface OrderService {

    OrderDTO placeOrder(OrderRequest request);

    List<OrderDTO> getCustomerOrders();

    OrderDTO getOrderById(Long id);

    OrderDTO getOrderByNumber(String orderNumber);

    OrderDTO updateOrderStatus(Long id, OrderStatusUpdateRequest request);

    OrderDTO cancelOrder(Long id);

    List<OrderDTO> getAllOrders();
}
