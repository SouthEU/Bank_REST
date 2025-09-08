package com.example.bankcards.controller;

import com.example.bankcards.dto.*;
import com.example.bankcards.entity.User;
import com.example.bankcards.service.CardService;
import com.example.bankcards.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Users", description = "Operations for users")
public class UserController {

    private final UserService userService;
    private final CardService cardService;

    @GetMapping("/cards")
    @Operation(summary = "Get user cards", description = "Get user cards")
    @Parameters({
            @Parameter(
                    name = "page",
                    description = "Page number (0-based)",
                    example = "0"
            ),
            @Parameter(
                    name = "size",
                    description = "Page size",
                    example = "10"
            ),
            @Parameter(
                    name = "sortBy",
                    description = "Field to sort by",
                    schema = @Schema(
                            allowableValues = {"id", "balance", "createdAt", "expirationDate", "status"},
                            type = "string"
                    ),
                    example = "id"
            ),
            @Parameter(
                    name = "sortDir",
                    description = "Sort direction: asc or desc",
                    schema = @Schema(
                            allowableValues = {"asc", "desc"},
                            type = "string"
                    ),
                    example = "asc"
            )
    })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = CardResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "User not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<Page<CardResponseDto>> getAllCards(@Parameter(description = "Page number") @RequestParam(defaultValue = "0") int page,
                                                             @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size,
                                                             @Parameter(description = "Sort by field") @RequestParam(defaultValue = "id") String sortBy,
                                                             @Parameter(description = "Sort direction") @RequestParam(defaultValue = "asc") String sortDir,
                                                             Principal principal) {
        log.info("Get user cards");
        User user = (User) ((UsernamePasswordAuthenticationToken) principal).getPrincipal();
        return ResponseEntity.ok(cardService.getAllCardsByUser(page, size, sortBy, sortDir, user));
    }

    @PostMapping("/request/{cardId}")
    @Operation(summary = "Send card request", description = "Send card request")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = CardProcessDto.class))),
            @ApiResponse(responseCode = "409", description = "Not your card", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<CardProcessDto> sendRequest(@PathVariable("cardId") Long cardId, Principal principal) {
        log.info("Send card request {}", cardId);
        User user = (User) ((UsernamePasswordAuthenticationToken) principal).getPrincipal();
        return ResponseEntity.ok(cardService.sendRequest(cardId, user));
    }

    @PostMapping("/transfer")
    @Operation(summary = "Send transfer", description = "Send transfer")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = TransferDto.class))),
            @ApiResponse(responseCode = "409", description = "Not your card or card not found or blocked", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<TransferDto> sendTransfer(@Validated @RequestBody TransferRequestDto transferRequestDto, Principal principal) {
        log.info("Send transfer {}", transferRequestDto);
        User user = (User) ((UsernamePasswordAuthenticationToken) principal).getPrincipal();
        return ResponseEntity.ok(cardService.sendTransfer(transferRequestDto, user));
    }

    @GetMapping("/balance")
    @Operation(summary = "Get user balance", description = "Get user balance")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = UserWithBalanceDto.class))),
            @ApiResponse(responseCode = "404", description = "User not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<UserWithBalanceDto> getUserWithBalance(Principal principal) {
        log.info("Get user balance");
        User user = (User) ((UsernamePasswordAuthenticationToken) principal).getPrincipal();
        return ResponseEntity.ok(userService.getUserWithBalance(user.getId()));
    }

}
