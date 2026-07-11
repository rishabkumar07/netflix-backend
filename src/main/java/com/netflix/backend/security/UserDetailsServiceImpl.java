package com.netflix.backend.security;

import com.netflix.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Tells Spring Security how to load a user by username (email, in our case).
 *
 * .NET equivalent: implementing IUserStore<TUser> for ASP.NET Core Identity.
 *
 * Kept separate from SecurityConfig to avoid circular bean dependency:
 *   SecurityConfig → JwtAuthFilter → UserDetailsService → UserRepository  ✓
 *   (if UserDetailsService were a @Bean inside SecurityConfig that also receives JwtAuthFilter,
 *   Spring would see a cycle)
 */
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email)
                .map(user -> User.builder()
                        .username(user.getEmail())
                        .password(user.getPassword())
                        .roles("USER")
                        .build())
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));
    }
}
