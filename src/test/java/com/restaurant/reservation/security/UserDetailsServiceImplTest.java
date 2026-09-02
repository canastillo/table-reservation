package com.restaurant.reservation.security;

import com.restaurant.reservation.domain.models.User;
import com.restaurant.reservation.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserDetailsServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;

    @Test
    void loadUserByUsername_shouldReturnUserDetails() {
        User user = new User("Admin", "admin@example.com", "encodedPassword");
        when(userRepository.findByEmail("admin@example.com"))
                .thenReturn(Optional.of(user));

        UserDetails result = userDetailsService.loadUserByUsername("admin@example.com");

        assertThat(result.getUsername()).isEqualTo("admin@example.com");
    }

    @Test
    void loadUserByUsername_shouldThrowWhenUserNotFound() {
        when(userRepository.findByEmail("nobody@example.com"))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> userDetailsService.loadUserByUsername("nobody@example.com"))
                .isInstanceOf(UsernameNotFoundException.class);
    }
}