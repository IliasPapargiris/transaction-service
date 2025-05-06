package com.okto.transactionservice.apidoc;

import com.okto.transactionservice.dto.LoginRequestDTO;
import com.okto.transactionservice.dto.LoginResponseDTO;
import com.okto.transactionservice.util.JsonExamples;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.*;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;

public interface AuthApiDocumentation {

    @Operation(
            summary = "User login",
            description = "Authenticates the user and returns a JWT token"
    )
    @ApiResponse(responseCode = "200", description = "Successful login",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = String.class)))
    @ApiResponse(responseCode = "400", description = "Validation error",
            content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(value = JsonExamples.VALIDATION_ERROR_JSON)))
    @ApiResponse(responseCode = "401", description = "Invalid credentials",
            content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(value = JsonExamples.INVALID_CREDENTIALS_JSON)))
    @PostMapping("/api/auth/login")
    ResponseEntity<LoginResponseDTO> login(
            @RequestBody(
                    description = "Login request containing email and password",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = LoginRequestDTO.class)
                    )
            )
            LoginRequestDTO request
    );

}
