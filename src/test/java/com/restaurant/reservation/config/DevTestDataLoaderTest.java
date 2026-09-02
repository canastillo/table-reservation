package com.restaurant.reservation.config;

import com.restaurant.reservation.domain.enums.RoleType;
import com.restaurant.reservation.domain.models.Role;
import com.restaurant.reservation.repository.RoleRepository;
import com.restaurant.reservation.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DevTestDataLoaderTest {

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private DevTestDataLoader devTestDataLoader;

    @Test
    void run_shouldCreateRolesWhenTheyDoNotExist() throws Exception {
        when(roleRepository.findByName(RoleType.ROLE_ADMIN)).thenReturn(Optional.empty());
        when(roleRepository.findByName(RoleType.ROLE_USER)).thenReturn(Optional.empty());

        // Avoid user creation for this test
        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        devTestDataLoader.run();

        verify(roleRepository).save(new Role(RoleType.ROLE_ADMIN));
        verify(roleRepository).save(new Role(RoleType.ROLE_USER));
    }

    @Test
    void run_shouldPopulateWithDefaultDataWhenDatabaseIsEmpty() throws Exception {
        Role adminRole = new Role(RoleType.ROLE_ADMIN);
        Role userRole = new Role(RoleType.ROLE_USER);

        when(roleRepository.findByName(RoleType.ROLE_ADMIN))
                .thenReturn(Optional.empty())
                .thenReturn(Optional.of(adminRole));

        when(roleRepository.findByName(RoleType.ROLE_USER))
                .thenReturn(Optional.empty())
                .thenReturn(Optional.of(userRole));

        when(roleRepository.save(adminRole)).thenReturn(adminRole);
        when(roleRepository.save(userRole)).thenReturn(userRole);

        when(userRepository.existsByEmail("admin@admin.com")).thenReturn(false);
        when(userRepository.existsByEmail("user@user.com")).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encoded");

        devTestDataLoader.run();

        verify(roleRepository, times(2)).findByName(RoleType.ROLE_ADMIN);
        verify(roleRepository, times(2)).findByName(RoleType.ROLE_USER);
        verify(roleRepository).save(adminRole);
        verify(roleRepository).save(userRole);
        verify(userRepository).save(argThat(user -> user.getEmail().equals("admin@admin.com")));
        verify(userRepository).save(argThat(user -> user.getEmail().equals("user@user.com")));
    }

    @Test
    void run_shouldNotDuplicateExistingRolesAndUsers() throws Exception {
        when(roleRepository.findByName(RoleType.ROLE_ADMIN)).thenReturn(Optional.of(new Role(RoleType.ROLE_ADMIN)));
        when(roleRepository.findByName(RoleType.ROLE_USER)).thenReturn(Optional.of(new Role(RoleType.ROLE_USER)));
        when(userRepository.existsByEmail("admin@admin.com")).thenReturn(true);
        when(userRepository.existsByEmail("user@user.com")).thenReturn(true);

        devTestDataLoader.run();

        verify(roleRepository, never()).save(any(Role.class));
        verify(userRepository, never()).save(any());
    }
}