package com.restaurant.reservation.security;

import com.restaurant.reservation.repository.UserRepository;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Loads a user by the identifier provided during authentication.
     * <p>
     * Although the parameter is named {@code username} to conform to the
     * {@link UserDetailsService} interface, in this system the identifier is
     * actually the user's email address. The method delegates to
     * {@code userRepository.findByEmail(username)}.
     *
     * @param username the email address of the user to load
     * @return the corresponding {@link UserDetails} instance
     * @throws UsernameNotFoundException if no user exists with the given email
     */
    @Override
    @Transactional
    @NonNull
    public UserDetails loadUserByUsername(@NonNull String username) throws UsernameNotFoundException {
        return userRepository.findByEmail(username)
               .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
    }
}
