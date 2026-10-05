package com.harimart.service.impl;

import com.harimart.dto.AuthRequest;
import com.harimart.dto.AuthResponse;
import com.harimart.dto.RegisterRequest;
import com.harimart.dto.UserDTO;
import com.harimart.entity.Address;
import com.harimart.entity.Cart;
import com.harimart.entity.User;
import com.harimart.enums.Role;
import com.harimart.enums.UserStatus;
import com.harimart.exception.BadRequestException;
import com.harimart.exception.ResourceNotFoundException;
import com.harimart.exception.UnauthorizedException;
import com.harimart.repository.AddressRepository;
import com.harimart.repository.CartRepository;
import com.harimart.repository.UserRepository;
import com.harimart.security.JwtUtils;
import com.harimart.service.AuthService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final AddressRepository addressRepository;
    private final CartRepository cartRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    public AuthServiceImpl(UserRepository userRepository,
                           AddressRepository addressRepository,
                           CartRepository cartRepository,
                           PasswordEncoder passwordEncoder,
                           JwtUtils jwtUtils) {
        this.userRepository = userRepository;
        this.addressRepository = addressRepository;
        this.cartRepository = cartRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
    }

    @Override
    @Transactional
    public UserDTO register(RegisterRequest request) {
        if (request.getConfirmPassword() != null && !request.getPassword().equals(request.getConfirmPassword())) {
            throw new BadRequestException("Passwords do not match");
        }

        if (userRepository.existsByEmail(request.getEmail().trim().toLowerCase())) {
            throw new BadRequestException("Email is already registered. Please sign in or use a different email.");
        }

        if (userRepository.existsByPhone(request.getPhone().trim())) {
            throw new BadRequestException("Phone number is already registered.");
        }

        User user = new User();
        user.setName(request.getName().trim());
        user.setEmail(request.getEmail().trim().toLowerCase());
        user.setPhone(request.getPhone().trim());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.ROLE_CUSTOMER);
        user.setStatus(UserStatus.ACTIVE);

        User savedUser = userRepository.save(user);

        // Create empty shopping cart for user
        Cart cart = new Cart(savedUser);
        cartRepository.save(cart);

        // Save default address if provided
        if (StringUtils.hasText(request.getAddressLine()) && StringUtils.hasText(request.getCity())) {
            Address address = new Address();
            address.setUser(savedUser);
            address.setAddressLine(request.getAddressLine().trim());
            address.setCity(request.getCity().trim());
            address.setState(request.getState() != null ? request.getState().trim() : "");
            address.setPincode(request.getPincode() != null ? request.getPincode().trim() : "");
            address.setCountry(request.getCountry() != null ? request.getCountry().trim() : "India");
            address.setIsDefault(true);
            addressRepository.save(address);
        }

        return mapToDTO(savedUser);
    }

    @Override
    public AuthResponse login(AuthRequest request) {
        String identifier = request.getEmailOrPhone().trim();

        User user = userRepository.findByEmailOrPhone(identifier, identifier)
                .orElseThrow(() -> new UnauthorizedException("Invalid email/phone or password"));

        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new UnauthorizedException("Your account is inactive. Please contact customer support.");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new UnauthorizedException("Invalid email/phone or password");
        }

        String token = jwtUtils.generateToken(user.getEmail(), user.getRole().name(), user.getId());
        return new AuthResponse(token, mapToDTO(user));
    }

    @Override
    public UserDTO getCurrentUser() {
        User user = getCurrentAuthenticatedUser();
        return mapToDTO(user);
    }

    @Override
    public User getCurrentAuthenticatedUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            throw new UnauthorizedException("User is not authenticated");
        }

        String username = auth.getName();
        return userRepository.findByEmailOrPhone(username, username)
                .orElseThrow(() -> new ResourceNotFoundException("Authenticated user not found"));
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
}
