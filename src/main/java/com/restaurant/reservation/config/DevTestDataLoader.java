package com.restaurant.reservation.config;

import com.restaurant.reservation.domain.enums.RoleType;
import com.restaurant.reservation.domain.models.Role;
import com.restaurant.reservation.domain.models.User;
import com.restaurant.reservation.repository.RoleRepository;
import com.restaurant.reservation.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * Loads initial roles and users for development and test environments.
 * Activated only when the "dev" or "test" profile is active.
 */
@Component
@Profile({"dev", "test"})
public class DevTestDataLoader implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DevTestDataLoader(RoleRepository roleRepository,
                             UserRepository userRepository,
                             PasswordEncoder passwordEncoder) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        createRolesIfNotFound();
        createDefaultUsersIfNotFound();
    }

    private void createRolesIfNotFound() {
        if (roleRepository.findByName(RoleType.ROLE_ADMIN).isEmpty()) {
            roleRepository.save(new Role(RoleType.ROLE_ADMIN));
        }

        if (roleRepository.findByName(RoleType.ROLE_USER).isEmpty()) {
            roleRepository.save(new Role(RoleType.ROLE_USER));
        }
    }

    private void createDefaultUsersIfNotFound() {
        if (!userRepository.existsByEmail("admin@admin.com")) {
            User user = new User("Admin", "admin@admin.com", passwordEncoder.encode("admin"));
            user.setRoles(Set.of(roleRepository.findByName(RoleType.ROLE_ADMIN).orElseThrow()));
            userRepository.save(user);
        }

        if (!userRepository.existsByEmail("user@user.com")) {
            User user = new User("User", "user@user.com", passwordEncoder.encode("user"));
            user.setRoles(Set.of(roleRepository.findByName(RoleType.ROLE_USER).orElseThrow()));
            userRepository.save(user);
        }
    }
}
