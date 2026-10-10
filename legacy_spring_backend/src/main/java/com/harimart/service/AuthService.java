package com.harimart.service;

import com.harimart.dto.AuthRequest;
import com.harimart.dto.AuthResponse;
import com.harimart.dto.RegisterRequest;
import com.harimart.dto.UserDTO;
import com.harimart.entity.User;

public interface AuthService {

    AuthResponse login(AuthRequest request);

    UserDTO register(RegisterRequest request);

    UserDTO getCurrentUser();

    User getCurrentAuthenticatedUser();
}
