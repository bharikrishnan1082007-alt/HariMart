package com.harimart.service;

import com.harimart.dto.AddressDTO;
import com.harimart.dto.OrderDTO;
import com.harimart.dto.UserDTO;

import java.util.List;

public interface UserService {

    List<UserDTO> getAllUsers();

    UserDTO getUserById(Long id);

    UserDTO updateUserProfile(UserDTO dto);

    UserDTO toggleUserStatus(Long id);

    List<OrderDTO> getUserOrders(Long userId);

    List<AddressDTO> getUserAddresses();

    AddressDTO addAddress(AddressDTO addressDTO);

    void deleteAddress(Long addressId);
}
