package com.restaurant.reservation.config;

import com.restaurant.reservation.domain.enums.RoleType;
import com.restaurant.reservation.domain.models.Role;
import com.restaurant.reservation.domain.models.User;
import com.restaurant.reservation.repository.RoleRepository;
import com.restaurant.reservation.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProdDataSeederTest {
    @Mock
    private RoleRepository roleRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private ProdDataSeeder createSeeder(String email, String password, String fullName) {
        ProdProperties props = new ProdProperties(email, password, fullName);
        return new ProdDataSeeder(roleRepository, userRepository, passwordEncoder, props);
    }

    @Test
    void run_shouldCreateAdminWhenVariablesPresent() {
        ProdDataSeeder seeder = createSeeder("admin@example.com", "secret123", "Admin");
        Role adminRole = new Role(RoleType.ROLE_ADMIN);

        when(roleRepository.findByName(RoleType.ROLE_ADMIN))
                .thenReturn(Optional.empty())
                .thenReturn(Optional.of(adminRole));

        when(roleRepository.findByName(RoleType.ROLE_USER)).thenReturn(Optional.empty());
        when(userRepository.existsByEmail("admin@example.com")).thenReturn(false);
        when(passwordEncoder.encode("secret123")).thenReturn("encoded");

        seeder.run((String) null);

        verify(roleRepository).save(new Role(RoleType.ROLE_ADMIN));
        verify(roleRepository).save(new Role(RoleType.ROLE_USER));
        verify(userRepository).save(argThat(user -> user.getEmail().equals("admin@example.com")));
    }

    @Test
    void run_shouldNotDuplicateRolesAndAdminWhenAlreadyExist() {
        ProdDataSeeder seeder = createSeeder("admin@example.com", "secret123", "Admin");

        when(roleRepository.findByName(RoleType.ROLE_ADMIN)).thenReturn(Optional.of(new Role(RoleType.ROLE_ADMIN)));
        when(roleRepository.findByName(RoleType.ROLE_USER)).thenReturn(Optional.of(new Role(RoleType.ROLE_USER)));
        when(userRepository.existsByEmail("admin@example.com")).thenReturn(true);

        seeder.run((String) null);

        verify(roleRepository, never()).save(any(Role.class));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void run_shouldThrowWhenCredentialsMissing() {
        ProdDataSeeder seeder = createSeeder("", "", "");

        assertThatThrownBy(() -> seeder.run((String) null))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("ADMIN_EMAIL and ADMIN_PASSWORD");

        verify(userRepository, never()).save(any(User.class));
    }
}