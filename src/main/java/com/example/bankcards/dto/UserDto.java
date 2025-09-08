package com.example.bankcards.dto;

import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.User;

public record UserDto(
        Long id,
        String username,
        String firstName,
        String lastName,
        String email,
        String phone,
        String role,
        Boolean isActive
) {
    public static UserDto fromEntity(User user) {
        return new UserDto(
                user.getId(),
                user.getUsername(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getPhone(),
                user.getRole().toString(),
                user.getIsActive()
        );
    }
}
