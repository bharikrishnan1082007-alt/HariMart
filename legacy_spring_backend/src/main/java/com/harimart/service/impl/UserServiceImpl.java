package com.harimart.service.impl;

import com.harimart.dto.AddressDTO;
import com.harimart.dto.OrderDTO;
import com.harimart.dto.OrderItemDTO;
import com.harimart.dto.UserDTO;
import com.harimart.entity.Address;
import com.harimart.entity.Order;
import com.harimart.entity.OrderItem;
import com.harimart.entity.User;
import com.harimart.enums.UserStatus;
import com.harimart.exception.BadRequestException;
import com.harimart.exception.ResourceNotFoundException;
import com.harimart.repository.AddressRepository;
import com.harimart.repository.OrderItemRepository;
import com.harimart.repository.OrderRepository;
import com.harimart.repository.UserRepository;
import com.harimart.service.AuthService;
import com.harimart.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final AddressRepository addressRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final AuthService authService;

    public UserServiceImpl(UserRepository userRepository,
                           AddressRepository addressRepository,
                           OrderRepository orderRepository,
                           OrderItemRepository orderItemRepository,
                           AuthService authService) {
        this.userRepository = userRepository;
        this.addressRepository = addressRepository;
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.authService = authService;
    }

    @Override
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public UserDTO getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        return mapToDTO(user);
    }

    @Override
    @Transactional
    public UserDTO updateUserProfile(UserDTO dto) {
        User user = authService.getCurrentAuthenticatedUser();

        if (dto.getName() != null && !dto.getName().trim().isEmpty()) {
            user.setName(dto.getName().trim());
        }

        if (dto.getPhone() != null && !dto.getPhone().trim().isEmpty()) {
            if (!dto.getPhone().trim().equals(user.getPhone()) && userRepository.existsByPhone(dto.getPhone().trim())) {
                throw new BadRequestException("Phone number is already registered to another account");
            }
            user.setPhone(dto.getPhone().trim());
        }

        User updated = userRepository.save(user);
        return mapToDTO(updated);
    }

    @Override
    @Transactional
    public UserDTO toggleUserStatus(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        user.setStatus(user.getStatus() == UserStatus.ACTIVE ? UserStatus.INACTIVE : UserStatus.ACTIVE);
        User updated = userRepository.save(user);
        return mapToDTO(updated);
    }

    @Override
    public List<OrderDTO> getUserOrders(Long userId) {
        return orderRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(this::mapOrderToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<AddressDTO> getUserAddresses() {
        User user = authService.getCurrentAuthenticatedUser();
        return addressRepository.findByUserId(user.getId()).stream()
                .map(this::mapAddressToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public AddressDTO addAddress(AddressDTO addressDTO) {
        User user = authService.getCurrentAuthenticatedUser();

        Address address = new Address();
        address.setUser(user);
        address.setAddressLine(addressDTO.getAddressLine().trim());
        address.setCity(addressDTO.getCity().trim());
        address.setState(addressDTO.getState() != null ? addressDTO.getState().trim() : "");
        address.setPincode(addressDTO.getPincode() != null ? addressDTO.getPincode().trim() : "");
        address.setCountry(addressDTO.getCountry() != null ? addressDTO.getCountry().trim() : "India");
        address.setIsDefault(addressDTO.getIsDefault() != null ? addressDTO.getIsDefault() : false);

        if (Boolean.TRUE.equals(address.getIsDefault())) {
            // Unset previous defaults
            List<Address> existing = addressRepository.findByUserId(user.getId());
            for (Address a : existing) {
                a.setIsDefault(false);
                addressRepository.save(a);
            }
        }

        Address saved = addressRepository.save(address);
        return mapAddressToDTO(saved);
    }

    @Override
    @Transactional
    public void deleteAddress(Long addressId) {
        User user = authService.getCurrentAuthenticatedUser();
        Address address = addressRepository.findByIdAndUserId(addressId, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Address not found"));
        addressRepository.delete(address);
    }

    private UserDTO mapToDTO(User user) {
        return new UserDTO(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getPhone(),
                user.getRole(),
                user.getStatus(),
                user.getCreatedAt()
        );
    }

    private AddressDTO mapAddressToDTO(Address a) {
        return new AddressDTO(
                a.getId(),
                a.getAddressLine(),
                a.getCity(),
                a.getState(),
                a.getPincode(),
                a.getCountry(),
                a.getIsDefault()
        );
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
