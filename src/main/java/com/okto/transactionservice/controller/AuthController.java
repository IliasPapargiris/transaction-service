package com.okto.transactionservice.controller;

import com.okto.transactionservice.apidoc.AuthApiDocumentation;
import com.okto.transactionservice.dto.LoginRequestDTO;
import com.okto.transactionservice.dto.LoginResponseDTO;
import com.okto.transactionservice.security.CustomUserDetails;
import com.okto.transactionservice.security.JwtTokenProvider;
import com.okto.transactionservice.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController implements AuthApiDocumentation {

    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO request)  {

        CustomUserDetails customUserDetails = userService.loadCustomUserByEmail(request.getEmail());

        // Check password
        if (!userService.passwordMatches(request.getPassword(), customUserDetails.getPassword())) {
            throw new BadCredentialsException("Invalid credentials");
        }

        // Generate token
        String token = jwtTokenProvider.generateToken(customUserDetails);

        return ResponseEntity.ok(new LoginResponseDTO(token));
    }
}
