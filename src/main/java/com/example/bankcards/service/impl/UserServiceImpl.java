package com.example.bankcards.service.impl;

import com.example.bankcards.dto.JWTResponse;
import com.example.bankcards.dto.LoginRequest;
import com.example.bankcards.dto.UserDto;
import com.example.bankcards.dto.UserWithBalanceDto;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.user.UserAlreadyActiveException;
import com.example.bankcards.exception.user.UserAlreadyDeactivatedException;
import com.example.bankcards.exception.user.UserAlreadyHasRoleException;
import com.example.bankcards.exception.user.UserNotFoundException;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.security.JWT.JwtUtil;
import com.example.bankcards.service.UserService;
import com.example.bankcards.util.PageUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;
    private final CardRepository cardRepository;

    private final List<String> allowedSortFields = Arrays.asList("id", "username", "role");

    @Override
    public JWTResponse login(LoginRequest loginRequest) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.username(), loginRequest.password())
        );

        UserDetails userDetails = userDetailsService.loadUserByUsername(loginRequest.username());

        return new JWTResponse(jwtUtil.generateToken(userDetails), jwtUtil.generateRefreshToken(userDetails));
    }

    @Override
    public Page<UserDto> getAllUsers(int page, int size, String sortBy, String sortDir) {
        Pageable pageable = PageUtil.setPage(page, size, sortBy, sortDir, allowedSortFields);
        return userRepository.findAll(pageable).map(UserDto::fromEntity);
    }

    @Override
    public UserDto deactivateUser(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found"));
        if(!user.getIsActive()){
            throw new UserAlreadyDeactivatedException("User already deactivated");
        }

        user.setIsActive(false);

        return UserDto.fromEntity(userRepository.save(user));
    }

    @Override
    public UserDto activateUser(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found"));
        if(user.getIsActive()){
            throw new UserAlreadyActiveException("User already activated");
        }

        user.setIsActive(true);

        return UserDto.fromEntity(userRepository.save(user));
    }

    @Override
    public UserDto addRoleToUser(Long userId, User.Role roleName) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found"));
        if(user.getRole().equals(roleName)){
            throw new UserAlreadyHasRoleException("User already has this role");
        }

        user.setRole(roleName);

        return UserDto.fromEntity(userRepository.save(user));
    }

    @Override
    public UserWithBalanceDto getUserWithBalance(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found"));

        Double balance = cardRepository.getTotalBalanceByUserId(userId);

        return new UserWithBalanceDto(UserDto.fromEntity(user), balance);
    }
}
