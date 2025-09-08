package com.example.bankcards.service;

import com.example.bankcards.dto.JWTResponse;
import com.example.bankcards.dto.LoginRequest;
import com.example.bankcards.dto.UserDto;
import com.example.bankcards.dto.UserWithBalanceDto;
import com.example.bankcards.entity.User;
import org.springframework.data.domain.Page;

public interface UserService {
    JWTResponse login(LoginRequest loginRequest);
    Page<UserDto> getAllUsers(int page, int size, String sortBy, String sortDir);
    UserDto deactivateUser(Long userId);
    UserDto activateUser(Long userId);
    UserDto addRoleToUser(Long userId, User.Role roleName);
    UserWithBalanceDto getUserWithBalance(Long userId);
}
