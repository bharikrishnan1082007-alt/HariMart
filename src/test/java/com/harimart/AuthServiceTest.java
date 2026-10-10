package com.harimart;

import com.harimart.model.User;
import com.harimart.util.PasswordUtil;
import com.harimart.util.ValidationUtil;
import org.junit.Test;

import static org.junit.Assert.*;

public class AuthServiceTest {

    @Test
    public void testPasswordHashingAndVerification() {
        String raw = "SecurePass@2026";
        String hashed = PasswordUtil.hashPassword(raw);

        assertNotNull(hashed);
        assertTrue(hashed.startsWith("$2a$10$"));
        assertTrue(PasswordUtil.checkPassword(raw, hashed));
        assertFalse(PasswordUtil.checkPassword("WrongPass", hashed));
    }

    @Test
    public void testEmailAndPhoneValidation() {
        assertTrue(ValidationUtil.isValidEmail("buyer@harimart.com"));
        assertTrue(ValidationUtil.isValidEmail("john.doe+test@example.co.in"));
        assertFalse(ValidationUtil.isValidEmail("invalid-email"));
        assertFalse(ValidationUtil.isValidEmail("@missingusername.com"));

        assertTrue(ValidationUtil.isValidPhone("9876543210"));
        assertTrue(ValidationUtil.isValidPhone("+91 98765 43210"));
        assertFalse(ValidationUtil.isValidPhone("12345"));
    }

    @Test
    public void testUserRoles() {
        User admin = new User(1L, "Admin", "admin@harimart.com", "9999999999", "hash", "ADMIN", "ACTIVE", null);
        assertTrue(admin.isAdmin());
        assertFalse(admin.isSeller());
        assertFalse(admin.isBuyer());

        User seller = new User(2L, "Seller", "seller@harimart.com", "9999999998", "hash", "SELLER", "ACTIVE", null);
        assertFalse(seller.isAdmin());
        assertTrue(seller.isSeller());

        User buyer = new User(3L, "Buyer", "buyer@harimart.com", "9999999997", "hash", "BUYER", "ACTIVE", null);
        assertTrue(buyer.isBuyer());
    }
}
