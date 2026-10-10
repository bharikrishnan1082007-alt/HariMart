package com.harimart.service.impl;

import com.harimart.dto.AddressDTO;
import com.harimart.dto.OrderDTO;
import com.harimart.dto.OrderItemDTO;
import com.harimart.dto.OrderRequest;
import com.harimart.dto.OrderStatusUpdateRequest;
import com.harimart.entity.Address;
import com.harimart.entity.Cart;
import com.harimart.entity.CartItem;
import com.harimart.entity.Order;
import com.harimart.entity.OrderItem;
import com.harimart.entity.Product;
import com.harimart.entity.User;
import com.harimart.enums.OrderStatus;
import com.harimart.enums.PaymentStatus;
import com.harimart.enums.Role;
import com.harimart.exception.BadRequestException;
import com.harimart.exception.ResourceNotFoundException;
import com.harimart.exception.UnauthorizedException;
import com.harimart.repository.AddressRepository;
import com.harimart.repository.CartItemRepository;
import com.harimart.repository.CartRepository;
import com.harimart.repository.OrderItemRepository;
import com.harimart.repository.OrderRepository;
import com.harimart.repository.ProductRepository;
import com.harimart.service.AuthService;
import com.harimart.service.OrderService;
import com.harimart.service.PaymentService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final AddressRepository addressRepository;
    private final AuthService authService;
    private final PaymentService paymentService;

    @Value("${app.promo.code:HARI10}")
    private String promoCode;

    @Value("${app.promo.discount-percentage:10}")
    private int discountPercentage;

    @Value("${app.shipping.free-threshold:499.00}")
    private BigDecimal freeShippingThreshold;

    @Value("${app.shipping.flat-fee:49.00}")
    private BigDecimal flatShippingFee;

    public OrderServiceImpl(OrderRepository orderRepository,
                            OrderItemRepository orderItemRepository,
                            CartRepository cartRepository,
                            CartItemRepository cartItemRepository,
                            ProductRepository productRepository,
                            AddressRepository addressRepository,
                            AuthService authService,
                            PaymentService paymentService) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
        this.addressRepository = addressRepository;
        this.authService = authService;
        this.paymentService = paymentService;
    }

    @Override
    @Transactional
    public OrderDTO placeOrder(OrderRequest request) {
        User user = authService.getCurrentAuthenticatedUser();

        // 1. Resolve Shipping Address
        String shippingAddressStr = resolveShippingAddress(user, request);

        // 2. Prepare items for order
        List<ItemToOrder> itemsToProcess = new ArrayList<>();

        if (request.getDirectProductId() != null) {
            Product product = productRepository.findById(request.getDirectProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
            int qty = request.getDirectQuantity() != null && request.getDirectQuantity() > 0 ? request.getDirectQuantity() : 1;
            itemsToProcess.add(new ItemToOrder(product, qty));
        } else {
            Cart cart = cartRepository.findByUserId(user.getId())
                    .orElseThrow(() -> new BadRequestException("Shopping cart is empty"));
            List<CartItem> cartItems = cartItemRepository.findByCartId(cart.getId());
            if (cartItems.isEmpty()) {
                throw new BadRequestException("Your cart is empty. Add products before placing an order.");
            }
            for (CartItem ci : cartItems) {
                itemsToProcess.add(new ItemToOrder(ci.getProduct(), ci.getQuantity()));
            }
        }

        // 3. Stock validation & Subtotal calculation
        BigDecimal subtotal = BigDecimal.ZERO;
        for (ItemToOrder item : itemsToProcess) {
            Product product = productRepository.findById(item.product.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

            if (!"ACTIVE".equalsIgnoreCase(product.getStatus())) {
                throw new BadRequestException("Product '" + product.getName() + "' is no longer available.");
            }

            if (item.quantity > product.getStock()) {
                throw new BadRequestException("Insufficient stock for '" + product.getName() + "'. Available: " + product.getStock());
            }

            BigDecimal unitPrice = product.getFinalPrice();
            subtotal = subtotal.add(unitPrice.multiply(BigDecimal.valueOf(item.quantity)));
        }

        // 4. Calculate coupon discount
        BigDecimal discount = BigDecimal.ZERO;
        if (request.getCouponCode() != null && promoCode.equalsIgnoreCase(request.getCouponCode().trim())) {
            discount = subtotal.multiply(BigDecimal.valueOf(discountPercentage))
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        }

        // 5. Calculate delivery fee
        BigDecimal deliveryFee = BigDecimal.ZERO;
        if (subtotal.compareTo(freeShippingThreshold) < 0) {
            deliveryFee = flatShippingFee;
        }

        BigDecimal finalAmount = subtotal.subtract(discount).add(deliveryFee).max(BigDecimal.ZERO);

        // 6. Generate unique order number
        String orderNumber = generateOrderNumber();

        // 7. Process payment
        PaymentService.PaymentResult paymentResult = paymentService.processPayment(
                request.getPaymentMethod(),
                finalAmount,
                orderNumber
        );

        if (!paymentResult.isSuccessful()) {
            throw new BadRequestException("Payment failed: " + paymentResult.getMessage());
        }

        // 8. Create Order record
        Order order = new Order();
        order.setOrderNumber(orderNumber);
        order.setUser(user);
        order.setTotalAmount(subtotal);
        order.setDiscount(discount);
        order.setDeliveryCharge(deliveryFee);
        order.setFinalAmount(finalAmount);
        order.setPaymentMethod(request.getPaymentMethod());
        order.setPaymentStatus(paymentResult.getStatus());
        order.setOrderStatus(OrderStatus.PLACED);
        order.setShippingAddress(shippingAddressStr);

        Order savedOrder = orderRepository.save(order);

        // 9. Create Order Items & Deduct Stock transactionally
        List<OrderItem> orderItems = new ArrayList<>();
        for (ItemToOrder item : itemsToProcess) {
            Product product = item.product;
            // Deduct stock
            product.setStock(product.getStock() - item.quantity);
            productRepository.save(product);

            OrderItem orderItem = new OrderItem(
                    null,
                    savedOrder,
                    product,
                    item.quantity,
                    product.getFinalPrice()
            );
            orderItems.add(orderItemRepository.save(orderItem));
        }

        savedOrder.setItems(orderItems);

        // 10. Clear cart if order was placed from cart
        if (request.getDirectProductId() == null) {
            cartRepository.findByUserId(user.getId()).ifPresent(c -> cartItemRepository.deleteByCartId(c.getId()));
        }

        return mapToDTO(savedOrder);
    }

    @Override
    public List<OrderDTO> getCustomerOrders() {
        User user = authService.getCurrentAuthenticatedUser();
        return orderRepository.findByUserIdOrderByCreatedAtDesc(user.getId()).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public OrderDTO getOrderById(Long id) {
        User user = authService.getCurrentAuthenticatedUser();
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));

        // Customer can only view their own order; Admin can view any
        if (user.getRole() != Role.ROLE_ADMIN && !order.getUser().getId().equals(user.getId())) {
            throw new UnauthorizedException("You do not have permission to view this order");
        }

        return mapToDTO(order);
    }

    @Override
    public OrderDTO getOrderByNumber(String orderNumber) {
        User user = authService.getCurrentAuthenticatedUser();
        Order order = orderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with number: " + orderNumber));

        if (user.getRole() != Role.ROLE_ADMIN && !order.getUser().getId().equals(user.getId())) {
            throw new UnauthorizedException("You do not have permission to view this order");
        }

        return mapToDTO(order);
    }

    @Override
    @Transactional
    public OrderDTO updateOrderStatus(Long id, OrderStatusUpdateRequest request) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));

        order.setOrderStatus(request.getOrderStatus());
        if (request.getPaymentStatus() != null) {
            order.setPaymentStatus(request.getPaymentStatus());
        }

        // If delivered and payment was COD, update payment to COMPLETED
        if (order.getOrderStatus() == OrderStatus.DELIVERED && order.getPaymentStatus() == PaymentStatus.PENDING) {
            order.setPaymentStatus(PaymentStatus.COMPLETED);
        }

        Order updated = orderRepository.save(order);
        return mapToDTO(updated);
    }

    @Override
    @Transactional
    public OrderDTO cancelOrder(Long id) {
        User user = authService.getCurrentAuthenticatedUser();
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));

        if (user.getRole() != Role.ROLE_ADMIN && !order.getUser().getId().equals(user.getId())) {
            throw new UnauthorizedException("You do not have permission to cancel this order");
        }

        // Cancellation rules: Cannot cancel if PACKED, SHIPPED, OUT_FOR_DELIVERY, or DELIVERED
        if (order.getOrderStatus() == OrderStatus.SHIPPED ||
                order.getOrderStatus() == OrderStatus.OUT_FOR_DELIVERY ||
                order.getOrderStatus() == OrderStatus.DELIVERED) {
            throw new BadRequestException("Order cannot be cancelled after shipping. Current status: " + order.getOrderStatus());
        }

        if (order.getOrderStatus() == OrderStatus.CANCELLED) {
            throw new BadRequestException("Order is already cancelled.");
        }

        // Restore stock
        List<OrderItem> items = orderItemRepository.findByOrderId(order.getId());
        for (OrderItem item : items) {
            Product product = item.getProduct();
            product.setStock(product.getStock() + item.getQuantity());
            productRepository.save(product);
        }

        order.setOrderStatus(OrderStatus.CANCELLED);
        Order updated = orderRepository.save(order);
        return mapToDTO(updated);
    }

    @Override
    public List<OrderDTO> getAllOrders() {
        return orderRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    private String resolveShippingAddress(User user, OrderRequest request) {
        if (request.getAddressId() != null) {
            Address address = addressRepository.findByIdAndUserId(request.getAddressId(), user.getId())
                    .orElseThrow(() -> new BadRequestException("Selected address not found"));
            return formatAddress(address);
        }

        if (request.getNewAddress() != null && StringUtils.hasText(request.getNewAddress().getAddressLine())) {
            AddressDTO a = request.getNewAddress();
            Address address = new Address();
            address.setUser(user);
            address.setAddressLine(a.getAddressLine().trim());
            address.setCity(a.getCity() != null ? a.getCity().trim() : "");
            address.setState(a.getState() != null ? a.getState().trim() : "");
            address.setPincode(a.getPincode() != null ? a.getPincode().trim() : "");
            address.setCountry(a.getCountry() != null ? a.getCountry().trim() : "India");
            address.setIsDefault(a.getIsDefault() != null ? a.getIsDefault() : false);
            Address saved = addressRepository.save(address);
            return formatAddress(saved);
        }

        // Fallback to user's default address
        Address defaultAddr = addressRepository.findByUserIdAndIsDefaultTrue(user.getId())
                .orElse(null);

        if (defaultAddr != null) {
            return formatAddress(defaultAddr);
        }

        List<Address> userAddresses = addressRepository.findByUserId(user.getId());
        if (!userAddresses.isEmpty()) {
            return formatAddress(userAddresses.get(0));
        }

        throw new BadRequestException("A valid delivery address is required to place an order.");
    }

    private String formatAddress(Address a) {
        return String.format("%s, %s, %s - %s, %s",
                a.getAddressLine(), a.getCity(), a.getState(), a.getPincode(), a.getCountry());
    }

    private String generateOrderNumber() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyMMddHHmm"));
        int random = 1000 + new Random().nextInt(9000);
        return "HM" + timestamp + random;
    }

    private OrderDTO mapToDTO(Order order) {
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

        // Cancellable if status is PLACED or CONFIRMED
        dto.setCancellable(order.getOrderStatus() == OrderStatus.PLACED || order.getOrderStatus() == OrderStatus.CONFIRMED);

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

    private static class ItemToOrder {
        final Product product;
        final int quantity;

        ItemToOrder(Product product, int quantity) {
            this.product = product;
            this.quantity = quantity;
        }
    }
}
