package com.netflix.backend.service;

import com.netflix.backend.dto.request.LoginRequest;
import com.netflix.backend.dto.request.RegisterRequest;
import com.netflix.backend.dto.response.AuthResponse;
import com.netflix.backend.entity.User;
import com.netflix.backend.repository.UserRepository;
import com.netflix.backend.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Business logic for auth. Controllers stay thin — they validate input and delegate here.
 *
 * Interview angle: why is @Transactional on the service, not the controller?
 * → Transactions should wrap business units of work. A controller can call multiple service
 *   methods; each should manage its own transaction boundary. Putting it on the controller
 *   would create one giant transaction spanning unrelated DB calls.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Transactional
    public AuthResponse register(RegisterRequest req) {
        if (userRepository.existsByEmail(req.getEmail())) {
            throw new IllegalArgumentException("Email already registered: " + req.getEmail());
        }

        User user = User.builder()
                .email(req.getEmail())
                .password(passwordEncoder.encode(req.getPassword()))  // BCrypt, never store plaintext
                .displayName(req.getDisplayName())
                .build();

        userRepository.save(user);
        log.info("Registered new user: {}", req.getEmail());

        return buildAuthResponse(user);
    }

    public AuthResponse login(LoginRequest req) {
        User user = userRepository.findByEmail(req.getEmail())
                .orElseThrow(() -> new BadCredentialsException("Invalid email or password"));

        // passwordEncoder.matches() handles BCrypt comparison — never compare raw strings
        if (!passwordEncoder.matches(req.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Invalid email or password");
        }

        log.info("User logged in: {}", req.getEmail());
        return buildAuthResponse(user);
    }

    private AuthResponse buildAuthResponse(User user) {
        return AuthResponse.builder()
                .token(jwtUtil.generateToken(user.getEmail()))
                .email(user.getEmail())
                .displayName(user.getDisplayName())
                .build();
    }
}
