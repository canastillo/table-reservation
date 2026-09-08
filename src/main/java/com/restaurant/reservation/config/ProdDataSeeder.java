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

@Slf4j
@Component
@Profile("prod")
public class ProdDataSeeder implements CommandLineRunner {
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ProdProperties props;

    public ProdDataSeeder(RoleRepository roleRepository,
                          UserRepository userRepository,
                          PasswordEncoder passwordEncoder, ProdProperties props) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.props = props;
    }

    @Override
    public void run(String... args) {
        createRolesIfNotFound();

        if (StringUtils.hasText(props.email()) && StringUtils.hasText(props.password())) {
            createAdminIfNotFound();
        } else {
            throw new IllegalStateException("Admin credentials are required. Set ADMIN_EMAIL and ADMIN_PASSWORD");
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

    private void createAdminIfNotFound() {
        if (!userRepository.existsByEmail(props.email())) {
            String fullName = StringUtils.hasText(props.fullName()) ? props.fullName() : "Administrator";
            User admin = new User(fullName, props.email(), passwordEncoder.encode(props.password()));
            admin.setRoles(Set.of(roleRepository.findByName(RoleType.ROLE_ADMIN).orElseThrow()));

            userRepository.save(admin);

            log.info("Production admin user created with email: {}", props.email());
        }
    }
}
