package com.example.bankcards.service;

import com.example.bankcards.dto.*;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.user.*;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.security.JWT.JwtUtil;
import com.example.bankcards.service.UserService;
import com.example.bankcards.service.impl.UserServiceImpl;
import com.example.bankcards.util.PageUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private CardRepository cardRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .username("testuser")
                .firstName("Test")
                .lastName("User")
                .email("test@example.com")
                .role(User.Role.USER)
                .isActive(true)
                .createdAt(OffsetDateTime.now())
                .updatedAt(OffsetDateTime.now())
                .build();
    }

    @Test
    @DisplayName("Тест: успешный логин")
    void login_ShouldReturnJWTResponse_WhenCredentialsAreValid() {
        // Given
        LoginRequest loginRequest = new LoginRequest("testuser", "password");
        UserDetails userDetails = org.springframework.security.core.userdetails.User
                .withUsername("testuser")
                .password("password")
                .authorities(() -> "USER")
                .build();

        when(userDetailsService.loadUserByUsername("testuser")).thenReturn(userDetails);
        when(jwtUtil.generateToken(userDetails)).thenReturn("mock.jwt.token");
        when(jwtUtil.generateRefreshToken(userDetails)).thenReturn("mock.refresh.token");

        // When
        JWTResponse response = userService.login(loginRequest);

        // Then
        assertThat(response.token()).isEqualTo("mock.jwt.token");
        assertThat(response.refreshToken()).isEqualTo("mock.refresh.token");
        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    @DisplayName("Тест: логин — пользователь не найден (внутри Spring Security)")
    void login_ShouldThrowException_WhenInvalidCredentials() {
        // Given
        LoginRequest loginRequest = new LoginRequest("unknown", "wrong");
        when(userDetailsService.loadUserByUsername("unknown"))
                .thenThrow(new RuntimeException("User not found"));

        // When & Then
        assertThatThrownBy(() -> userService.login(loginRequest))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("User not found");

        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }


    @Test
    @DisplayName("Тест: получение всех пользователей")
    void getAllUsers_ShouldReturnPageOfUsers() {
        // Given
        Pageable pageable = PageRequest.of(0, 10, Sort.by("id").ascending());
        Page<User> userPage = new PageImpl<>(Arrays.asList(user), pageable, 1);

        when(userRepository.findAll(pageable)).thenReturn(userPage);

        // When
        Page<UserDto> result = userService.getAllUsers(0, 10, "id", "asc");

        // Then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).username()).isEqualTo("testuser");
        verify(userRepository, times(1)).findAll(pageable);
    }

    @Test
    @DisplayName("Тест: деактивация пользователя")
    void deactivateUser_ShouldDeactivate_WhenUserIsActive() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        // When
        UserDto result = userService.deactivateUser(1L);

        // Then
        assertThat(result.id()).isEqualTo(1L);
        verify(userRepository, times(1)).save(argThat(u -> !u.getIsActive()));
    }

    @Test
    @DisplayName("Тест: деактивация уже деактивированного")
    void deactivateUser_ShouldThrowUserAlreadyDeactivatedException_WhenUserIsInactive() {
        user.setIsActive(false);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> userService.deactivateUser(1L))
                .isInstanceOf(UserAlreadyDeactivatedException.class)
                .hasMessage("User already deactivated");
    }

    @Test
    @DisplayName("Тест: активация пользователя")
    void activateUser_ShouldActivate_WhenUserIsInactive() {
        user.setIsActive(false);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        UserDto result = userService.activateUser(1L);

        assertThat(result.isActive()).isTrue();
    }

    @Test
    @DisplayName("Тест: активация уже активного")
    void activateUser_ShouldThrowUserAlreadyActiveException_WhenUserIsActive() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> userService.activateUser(1L))
                .isInstanceOf(UserAlreadyActiveException.class)
                .hasMessage("User already activated");
    }


    @Test
    @DisplayName("Тест: назначение роли")
    void addRoleToUser_ShouldChangeRole_WhenDifferentRole() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        UserDto result = userService.addRoleToUser(1L, User.Role.ADMIN);

        assertThat(result.role()).isEqualTo("ADMIN");
        verify(userRepository, times(1)).save(argThat(u -> u.getRole() == User.Role.ADMIN));
    }

    @Test
    @DisplayName("Тест: назначение уже имеющейся роли")
    void addRoleToUser_ShouldThrowUserAlreadyHasRoleException_WhenSameRole() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> userService.addRoleToUser(1L, User.Role.USER))
                .isInstanceOf(UserAlreadyHasRoleException.class)
                .hasMessage("User already has this role");
    }

    @Test
    @DisplayName("Тест: получение пользователя с балансом")
    void getUserWithBalance_ShouldReturnUserAndTotalBalance() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(cardRepository.getTotalBalanceByUserId(1L)).thenReturn(25000.0);

        UserWithBalanceDto result = userService.getUserWithBalance(1L);

        assertThat(result.user().username()).isEqualTo("testuser");
        assertThat(result.balance()).isEqualTo(25000.0);
    }

    @Test
    @DisplayName("Тест: получение пользователя с балансом — пользователь не найден")
    void getUserWithBalance_ShouldThrowUserNotFoundException_WhenUserNotFound() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getUserWithBalance(999L))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessage("User not found");
    }
}
