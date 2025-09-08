package com.example.bankcards.controller;

import com.example.bankcards.dto.ErrorResponse;
import com.example.bankcards.dto.JWTResponse;
import com.example.bankcards.dto.LoginRequest;
import com.example.bankcards.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Auth", description = "Auth operations")
public class AuthController {
    private final UserService userService;

    @PostMapping("/login")
    @Operation(summary = "Login", description = "Login user/admin")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = JWTResponse.class))),
            @ApiResponse(responseCode = "401", description = "Invalid credentials", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<JWTResponse> login(@Validated @RequestBody LoginRequest loginRequest) {
        log.info("Request to login: {}", loginRequest.username());
        return ResponseEntity.ok(userService.login(loginRequest));
    }
}
