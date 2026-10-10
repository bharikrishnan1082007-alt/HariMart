package com.harimart.controller;

import com.harimart.dto.AddressDTO;
import com.harimart.dto.ApiResponse;
import com.harimart.dto.OrderDTO;
import com.harimart.dto.UserDTO;
import com.harimart.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<List<UserDTO>>> getAllUsers() {
        List<UserDTO> users = userService.getAllUsers();
        return ResponseEntity.ok(ApiResponse.ok(users));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<UserDTO>> getUserById(@PathVariable Long id) {
        UserDTO user = userService.getUserById(id);
        return ResponseEntity.ok(ApiResponse.ok(user));
    }

    @PutMapping("/profile")
    public ResponseEntity<ApiResponse<UserDTO>> updateUserProfile(@Valid @RequestBody UserDTO userDTO) {
        UserDTO updated = userService.updateUserProfile(userDTO);
        return ResponseEntity.ok(ApiResponse.ok("Profile updated successfully", updated));
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<UserDTO>> toggleUserStatus(@PathVariable Long id) {
        UserDTO updated = userService.toggleUserStatus(id);
        return ResponseEntity.ok(ApiResponse.ok("User status updated to " + updated.getStatus(), updated));
    }

    @GetMapping("/{id}/orders")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<List<OrderDTO>>> getUserOrders(@PathVariable Long id) {
        List<OrderDTO> orders = userService.getUserOrders(id);
        return ResponseEntity.ok(ApiResponse.ok(orders));
    }

    @GetMapping("/addresses")
    public ResponseEntity<ApiResponse<List<AddressDTO>>> getUserAddresses() {
        List<AddressDTO> addresses = userService.getUserAddresses();
        return ResponseEntity.ok(ApiResponse.ok(addresses));
    }

    @PostMapping("/addresses")
    public ResponseEntity<ApiResponse<AddressDTO>> addAddress(@Valid @RequestBody AddressDTO addressDTO) {
        AddressDTO created = userService.addAddress(addressDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Address added successfully", created));
    }

    @DeleteMapping("/addresses/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteAddress(@PathVariable Long id) {
        userService.deleteAddress(id);
        return ResponseEntity.ok(ApiResponse.ok("Address removed", null));
    }
}
