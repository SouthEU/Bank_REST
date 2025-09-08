package com.example.bankcards.security;

import com.example.bankcards.repository.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;

@Configuration
public class UserDetailsServiceConfig {

    @Bean
    public UserDetailsService userDetailsService(UserRepository userRepository) {
        return username -> userRepository.findByUsername(username)
                .map(user -> {
                    GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + user.getRole().name());

                    return new org.springframework.security.core.userdetails.User(
                            user.getUsername(),
                            user.getPassword(),
                            user.getIsActive(),
                            true,
                            true,
                            true,
                            List.of(authority)
                    );
                })
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }
}