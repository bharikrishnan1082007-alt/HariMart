package com.harimart;

import com.harimart.dto.AuthRequest;
import com.harimart.dto.AuthResponse;
import com.harimart.dto.RegisterRequest;
import com.harimart.dto.UserDTO;
import com.harimart.exception.BadRequestException;
import com.harimart.exception.UnauthorizedException;
import com.harimart.service.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("dev")
@Transactional
class AuthServiceTest {

    @Autowired
    private AuthService authService;

    @Test
    void testRegisterSuccess() {
        String unique = UUID.randomUUID().toString().substring(0, 6);
        RegisterRequest req = new RegisterRequest();
        req.setName("Test User");
        req.setEmail("user_" + unique + "@example.com");
        req.setPhone("91" + unique);
        req.setPassword("pass123");
        req.setConfirmPassword("pass123");
        req.setAddressLine("123 Test St");
        req.setCity("Mumbai");

        UserDTO user = authService.register(req);
        assertNotNull(user.getId());
        assertEquals("Test User", user.getName());
    }

    @Test
    void testRegisterDuplicateEmailThrowsBadRequest() {
        RegisterRequest req = new RegisterRequest();
        req.setName("Admin Duplicate");
        req.setEmail("admin@harimart.com"); // already seeded
        req.setPhone("9999999999");
        req.setPassword("pass123");
        req.setConfirmPassword("pass123");

        assertThrows(BadRequestException.class, () -> authService.register(req));
    }

    @Test
    void testLoginSuccess() {
        AuthRequest req = new AuthRequest("admin@harimart.com", "admin123");
        AuthResponse resp = authService.login(req);

        assertNotNull(resp.getToken());
        assertEquals("admin@harimart.com", resp.getUser().getEmail());
    }

    @Test
    void testLoginInvalidCredentialsThrowsUnauthorized() {
        AuthRequest req = new AuthRequest("admin@harimart.com", "wrongpass");
        assertThrows(UnauthorizedException.class, () -> authService.login(req));
    }
}
