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

   @Override
   @Transactional
   @NonNull
   public UserDetails loadUserByUsername(@NonNull String username) throws UsernameNotFoundException {
       return userRepository.findByUsername(username)
               .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
   }
}
