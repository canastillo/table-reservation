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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DemoDataSeederTest {
    @Mock
    private RoleRepository roleRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private DemoDataSeeder createSeeder(String adminEmail, String adminPassword,
                                        String userEmail, String userPassword) {

        DemoProperties props = new DemoProperties(
                new DemoProperties.Admin(adminEmail, adminPassword),
                new DemoProperties.User(userEmail, userPassword)
        );

        return new DemoDataSeeder(roleRepository, userRepository, passwordEncoder, props);
    }

    @Test
    void run_shouldCreateDemoUsersWhenVariablesPresent() {
        DemoDataSeeder seeder = createSeeder(
                "demo_admin@example.com", "demo123",
                "demo_user@example.com", "demo123"
        );

        when(roleRepository.findByName(RoleType.ROLE_ADMIN)).thenReturn(Optional.of(new Role(RoleType.ROLE_ADMIN)));
        when(roleRepository.findByName(RoleType.ROLE_USER)).thenReturn(Optional.of(new Role(RoleType.ROLE_USER)));
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encoded");

        seeder.run(null);

        verify(userRepository).save(argThat(user -> user.getEmail().equals("demo_admin@example.com")));
        verify(userRepository).save(argThat(user -> user.getEmail().equals("demo_user@example.com")));
    }

    @Test
    void run_shouldNotDuplicateUsersWhenTheyAlreadyExist() {
        DemoDataSeeder seeder = createSeeder(
                "demo_admin@example.com", "demo123",
                "demo_user@example.com", "demo123"
        );

        when(roleRepository.findByName(RoleType.ROLE_ADMIN)).thenReturn(Optional.of(new Role(RoleType.ROLE_ADMIN)));
        when(roleRepository.findByName(RoleType.ROLE_USER)).thenReturn(Optional.of(new Role(RoleType.ROLE_USER)));
        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        seeder.run((String) null);

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void run_shouldThrowWhenAdminCredentialsMissing() {
        DemoDataSeeder seeder = createSeeder("", "", "demo_user@example.com", "demo123");

        assertThatThrownBy(() -> seeder.run((String) null))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("ADMIN_EMAIL and ADMIN_PASSWORD");

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void run_shouldThrowWhenUserCredentialsMissing() {
        DemoDataSeeder seeder = createSeeder("demo_admin@example.com", "demo123", "", "");

        assertThatThrownBy(() -> seeder.run((String) null))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("USER_EMAIL and USER_PASSWORD");

        verify(userRepository, never()).save(any(User.class));
    }
}