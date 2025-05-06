package com.okto.transactionservice.service;

import com.okto.transactionservice.entity.User;
import com.okto.transactionservice.exception.UserNotFoundException;
import com.okto.transactionservice.repository.UserRepository;
import com.okto.transactionservice.security.CustomUserDetails;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private static final String DEFAULT_ADMIN_EMAIL = "admin@admin.com";
    private static final String DEFAULT_ADMIN_PASSWORD = "admin123";

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        log.debug("Attempting to load user by email: {}", email);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("User not found with email: {}", email);
                    return new UsernameNotFoundException("User not found with email: " + email);
                });

        log.debug("User found: id={}, role={}", user.getId(), user.getRole());
        return new CustomUserDetails(
                user.getId(),
                user.getEmail(),
                user.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()))
        );
    }

    public CustomUserDetails loadCustomUserByEmail(String email) {
        log.debug("Loading custom user details for email: {}", email);
        return (CustomUserDetails) loadUserByUsername(email);
    }

    public User getUserOrThrow(Long userId) {
        log.debug("Fetching user by ID: {}", userId);
        return userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.warn("User not found with ID: {}", userId);
                    return new UserNotFoundException(userId);
                });
    }

    public boolean passwordMatches(String rawPassword, String encodedPassword) {
        log.debug("Comparing raw password with stored encoded password");
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    // HACKY WAY TO AVOID CREATING A REGISTER USER END POINT
//    @PostConstruct
//    public void encodeAdminUserPassword() {
//        Optional<User> existingAdmin = userRepository.findByEmail(DEFAULT_ADMIN_EMAIL);
//
//        if (!existingAdmin.isEmpty()) {
//            String encodedPassword = passwordEncoder.encode(DEFAULT_ADMIN_PASSWORD);
//            User user = existingAdmin.get();
//            user.setPassword(encodedPassword);
//            userRepository.save(user);
//            log.info("✅ Default admin user inserted. Login using default credentials as documented in Swagger.");
//        }
//    }
}
