package com.okto.transactionservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginRequestDTO {

    @Schema(description = "The user's email address", example = "admin@admin.com")
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    private String email;


    @Schema(description = "The user's password", example = "admin123")
    @NotBlank(message = "Password is required")
    private String password;
}
