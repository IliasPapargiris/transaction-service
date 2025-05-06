package com.okto.transactionservice.fixtures;

import com.okto.transactionservice.entity.Role;
import com.okto.transactionservice.entity.User;
import org.springframework.security.crypto.password.PasswordEncoder;

public class UserFixture {

    private static UserFixture instance;

    private UserFixture() {}

    public static UserFixture getInstance() {
        if (instance == null) {
            instance = new UserFixture();
        }
        return instance;
    }

    public User getAdmin(PasswordEncoder encoder) {
        return User.builder()
                .name("Admin")
                .email("admin@example.com")
                .password(encoder.encode("securePassword"))
                .role(Role.ADMIN)
                .build();
    }

    public User getAdmin() {
        return User.builder()
                .name("Admin")
                .email("admin@example.com")
                .password("securePassword")
                .role(Role.ADMIN)
                .build();
    }
}
