package com.okto.transactionservice.service;

import com.okto.transactionservice.security.CustomUserDetails;
import com.okto.transactionservice.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Service responsible for handling user authentication logic.
 * Validates user credentials and generates JWT tokens.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;

    /**
     * Authenticates the user with the given email and password.
     *
     * @param email       user's email
     * @param rawPassword plaintext password
     * @return a signed JWT if credentials are valid
     * @throws BadCredentialsException if authentication fails
     */
    public String login(String email, String rawPassword) {
        log.debug("Attempting login for email={}", email);

        CustomUserDetails user = userService.loadCustomUserByEmail(email);

        if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
            log.warn("Failed login attempt for email={}", email);
            throw new BadCredentialsException("Invalid email or password");
        }

        String token = jwtTokenProvider.generateToken(user);
        log.info("Login successful for email={}", email);
        return token;
    }
}
