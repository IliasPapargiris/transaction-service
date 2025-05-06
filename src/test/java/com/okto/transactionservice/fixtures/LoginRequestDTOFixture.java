package com.okto.transactionservice.fixtures;

import com.okto.transactionservice.dto.LoginRequestDTO;

public class LoginRequestDTOFixture {

    public static LoginRequestDTO getValidAdminLogin() {
        return new LoginRequestDTO("admin@example.com", "securePassword");
    }

    public static LoginRequestDTO getInvalidLogin() {
        return new LoginRequestDTO("admin@example.com", "wrongPassword");
    }

    public static LoginRequestDTO withEmail(String email) {
        return new LoginRequestDTO(email, "securePassword");
    }

    public static LoginRequestDTO withPassword(String password) {
        return new LoginRequestDTO("admin@example.com", password);
    }
}
