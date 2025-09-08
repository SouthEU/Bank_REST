package com.example.bankcards.exception;

import com.example.bankcards.dto.ErrorResponse;
import com.example.bankcards.exception.card.*;
import com.example.bankcards.exception.request.RequestAlreadyApprovedException;
import com.example.bankcards.exception.request.RequestAlreadyDeniedException;
import com.example.bankcards.exception.request.RequestNotFoundException;
import com.example.bankcards.exception.user.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrity(DataIntegrityViolationException e) {
        ErrorResponse error = new ErrorResponse("INTEGRITY_VIOLATION", "Cannot assign role due to constraint violation");
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(AccessDeniedException e) {
        ErrorResponse error = new ErrorResponse("ACCESS_DENIED", "You don't have permission to perform this action");
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpected(Exception e) {
        ErrorResponse error = new ErrorResponse("INTERNAL_ERROR", "An unexpected error occurred");
        log.info("Unexpected error: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFound(UserNotFoundException e) {
        ErrorResponse error = new ErrorResponse("USER_NOT_FOUND", e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException e) {
        ErrorResponse error = new ErrorResponse("ILLEGAL_ARGUMENT", e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(CardNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleCardNotFound(CardNotFoundException e) {
        ErrorResponse error = new ErrorResponse("CARD_NOT_FOUND", e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(CardAlreadyActiveException.class)
    public ResponseEntity<ErrorResponse> handleCardAlreadyActive(CardAlreadyActiveException e) {
        ErrorResponse error = new ErrorResponse("CARD_ALREADY_ACTIVE", e.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(CardAlreadyBlockedException.class)
    public ResponseEntity<ErrorResponse> handleCardAlreadyBlocked(CardAlreadyBlockedException e) {
        ErrorResponse error = new ErrorResponse("CARD_ALREADY_BLOCKED", e.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(UserAlreadyDeactivatedException.class)
    public ResponseEntity<ErrorResponse> handleUserAlreadyDeactivated(UserAlreadyDeactivatedException e) {
        ErrorResponse error = new ErrorResponse("USER_ALREADY_DEACTIVATED", e.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(UserAlreadyActiveException.class)
    public ResponseEntity<ErrorResponse> handleUserAlreadyActivated(UserAlreadyActiveException e) {
        ErrorResponse error = new ErrorResponse("USER_ALREADY_ACTIVE", e.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(UserAlreadyHasRoleException.class)
    public ResponseEntity<ErrorResponse> handleUserAlreadyHasRole(UserAlreadyHasRoleException e) {
        ErrorResponse error = new ErrorResponse("USER_ALREADY_HAS_ROLE", e.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(RequestAlreadyApprovedException.class)
    public ResponseEntity<ErrorResponse> handleRequestAlreadyApproved(RequestAlreadyApprovedException e) {
        ErrorResponse error = new ErrorResponse("REQUEST_ALREADY_APPROVED", e.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(RequestAlreadyDeniedException.class)
    public ResponseEntity<ErrorResponse> handleRequestAlreadyDenied(RequestAlreadyDeniedException e) {
        ErrorResponse error = new ErrorResponse("REQUEST_ALREADY_DENIED", e.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(RequestNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleRequestNotFound(RequestNotFoundException e) {
        ErrorResponse error = new ErrorResponse("REQUEST_NOT_FOUND", e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(NotEnoughBalanceException.class)
    public ResponseEntity<ErrorResponse> handleNotEnoughBalance(NotEnoughBalanceException e) {
        ErrorResponse error = new ErrorResponse("NOT_ENOUGH_BALANCE", e.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(NotUserCardException.class)
    public ResponseEntity<ErrorResponse> handleNotUserCard(NotUserCardException e) {
        ErrorResponse error = new ErrorResponse("NOT_USER_CARD", e.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(CardBlockedException.class)
    public ResponseEntity<ErrorResponse> handleCardBlocked(CardBlockedException e) {
        ErrorResponse error = new ErrorResponse("CARD_BLOCKED", e.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }
}
