package com.example.bankcards.controller;

import com.example.bankcards.dto.CardProcessDto;
import com.example.bankcards.dto.CardResponseDto;
import com.example.bankcards.dto.ErrorResponse;
import com.example.bankcards.dto.UserDto;
import com.example.bankcards.entity.User;
import com.example.bankcards.service.CardService;
import com.example.bankcards.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Admin", description = "Operations for admin")
public class AdminController {

    private final CardService cardService;
    private final UserService userService;

    @PostMapping("/users/{userId}/cards")
    @Operation(summary = "Create card", description = "Create card for user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = CardResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "User not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<CardResponseDto> createCard(@RequestParam Long userId) {
        log.info("Create card for user {}", userId);
        return ResponseEntity.ok(cardService.createCard(userId));
    }

    @PatchMapping("/cards/{cardId}/block")
    @Operation(summary = "Block card", description = "Block card")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = CardResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Card not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Card already blocked", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<CardResponseDto> blockCard(@PathVariable Long cardId) {
        log.info("Block card {}", cardId);
        return ResponseEntity.ok(cardService.blockCard(cardId));
    }

    @PatchMapping("/cards/{cardId}/activate")
    @Operation(summary = "Activate card", description = "Activate card")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = CardResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Card not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Card already active", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<CardResponseDto> activateCard(@PathVariable Long cardId) {
        log.info("Activate card {}", cardId);
        return ResponseEntity.ok(cardService.activateCard(cardId));
    }

    @DeleteMapping("/cards/{cardId}")
    @Operation(summary = "Delete card", description = "Delete card")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "OK"),
            @ApiResponse(responseCode = "404", description = "Card not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<Void> deleteCard(@PathVariable Long cardId) {
        log.info("Delete card {}", cardId);
        cardService.deleteCard(cardId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/cards")
    @Operation(
            summary = "Get all cards with pagination and sorting",
            description = "Retrieve a paginated list of all cards"
    )
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
            @ApiResponse(
                    responseCode = "200",
                    description = "List of cards retrieved successfully",
                    content = @Content(
                            array = @ArraySchema(schema = @Schema(implementation = CardResponseDto.class))
                    )
            )
    })
    public ResponseEntity<Page<CardResponseDto>> getAllCards(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir
    ) {
        log.info("Get all cards with pagination and sorting");
        return ResponseEntity.ok(cardService.getAllCards(page, size, sortBy, sortDir));
    }

    @GetMapping("/users")
    @Operation(
            summary = "Get all users with pagination and sorting",
            description = "Retrieve a paginated list of all users"
    )
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
                            allowableValues = {"id", "username", "role"},
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
            @ApiResponse(
                    responseCode = "200",
                    description = "List of users retrieved successfully",
                    content = @Content(
                            array = @ArraySchema(schema = @Schema(implementation = UserDto.class))
                    )
            )
    })
    public ResponseEntity<Page<UserDto>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir
    ) {
        log.info("Get all users with pagination and sorting");
        return ResponseEntity.ok(userService.getAllUsers(page, size, sortBy, sortDir));
    }

    @PatchMapping("/users/{userId}/activate")
    @Operation(summary = "Activate user", description = "Activate user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "404", description = "User not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "User already active", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<UserDto> activateUser(@PathVariable Long userId) {
        return ResponseEntity.ok(userService.activateUser(userId));
    }

    @PatchMapping("/users/{userId}/deactivate")
    @Operation(summary = "Deactivate user", description = "Deactivate user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "404", description = "User not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "User already deactivated", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<UserDto> deactivateUser(@PathVariable Long userId) {
        log.info("Deactivate user with id: {}", userId);
        return ResponseEntity.ok(userService.deactivateUser(userId));
    }

    @PatchMapping("/users/{userId}/role")
    @Operation(summary = "Add role to user", description = "Add role to user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "404", description = "User not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "User already has this role", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<UserDto> addRoleToUser(@PathVariable Long userId, @RequestParam User.Role roleName) {
        log.info("Add role {} to user with id: {}", roleName, userId);
        return ResponseEntity.ok(userService.addRoleToUser(userId, roleName));
    }

    @GetMapping("/requests")
    @Operation(summary = "Get all card requests", description = "Get all card requests")
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
                            allowableValues = {"id", "status"},
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
            @ApiResponse(responseCode = "200", description = "OK")
    })
    public ResponseEntity<Page<CardProcessDto>> getAllCardRequests(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir
    ) {
        log.info("Get all card requests with pagination and sorting");
        return ResponseEntity.ok(cardService.getAllCardRequests(page, size, sortBy, sortDir));
    }

    @PatchMapping("/requests/{requestId}/approve")
    @Operation(summary = "Approve card request", description = "Approve card request")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "404", description = "Card request not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Card request already approved", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<CardProcessDto> approveRequest(@PathVariable Long requestId) {
        log.info("Approve card request with id: {}", requestId);
        return ResponseEntity.ok(cardService.approveRequest(requestId));
    }

    @PatchMapping("/requests/{requestId}/decline")
    @Operation(summary = "Decline card request", description = "Decline card request")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "404", description = "Card request not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Card request already declined", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<CardProcessDto> declineRequest(@PathVariable Long requestId) {
        log.info("Decline card request with id: {}", requestId);
        return ResponseEntity.ok(cardService.declineRequest(requestId));
    }
}
