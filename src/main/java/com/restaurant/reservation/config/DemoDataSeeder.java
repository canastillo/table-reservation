package com.restaurant.reservation.config;

import com.restaurant.reservation.domain.enums.RoleType;
import com.restaurant.reservation.domain.models.Role;
import com.restaurant.reservation.domain.models.User;
import com.restaurant.reservation.repository.RoleRepository;
import com.restaurant.reservation.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Set;

/**
 * Loads demo users for the "demo" profile, intended for public portfolio
 * or sandbox environments. Credentials are intentionally simple and public.
 */
@Slf4j
@Component
@Profile("demo")
public class DemoDataSeeder implements CommandLineRunner {
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final DemoProperties props;

    public DemoDataSeeder(RoleRepository roleRepository,
                          UserRepository userRepository,
                          PasswordEncoder passwordEncoder, DemoProperties props) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.props = props;
    }

    @Override
    public void run(String... args) {
        createRolesIfNotFound();

        if (StringUtils.hasText(props.admin().email()) && StringUtils.hasText(props.admin().password())) {
            createUserIfNotFound(props.admin().email(), props.admin().password(), RoleType.ROLE_ADMIN, "Demo Admin");
        } else {
            throw new IllegalStateException(
                    "Demo admin credentials are required. Set DEMO_ADMIN_EMAIL and DEMO_ADMIN_PASSWORD environment variables."
            );
        }

        if (StringUtils.hasText(props.user().email()) && StringUtils.hasText(props.user().password())) {
            createUserIfNotFound(props.user().email(), props.user().password(), RoleType.ROLE_USER, "Demo User");
        } else {
            log.warn("Demo user credentials not provided. Skipping demo user creation");
        }
    }

    private void createRolesIfNotFound() {
        if (roleRepository.findByName(RoleType.ROLE_ADMIN).isEmpty()) {
            roleRepository.save(new Role(RoleType.ROLE_ADMIN));
        }

        if (roleRepository.findByName(RoleType.ROLE_USER).isEmpty()) {
            roleRepository.save(new Role(RoleType.ROLE_USER));
        }
    }

    private void createUserIfNotFound(String email, String password, RoleType roleType, String fullName) {
        if (!userRepository.existsByEmail(email)) {
            User user = new User(fullName, email, passwordEncoder.encode(password));
            user.setRoles(Set.of(roleRepository.findByName(roleType).orElseThrow()));

            userRepository.save(user);

            log.info("Demo user created with email: {}", email);
        }
    }
}
