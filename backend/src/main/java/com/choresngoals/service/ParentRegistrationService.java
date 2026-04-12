package com.choresngoals.service;

import java.util.Locale;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.choresngoals.dto.RegisterParentRequest;
import com.choresngoals.dto.UserResponse;
import com.choresngoals.entity.User;
import com.choresngoals.entity.UserRole;
import com.choresngoals.repository.UserRepository;

@Service
public class ParentRegistrationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public ParentRegistrationService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public UserResponse registerParent(RegisterParentRequest request) {
        String email = normalizeEmail(request.email());

        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw new DuplicateEmailException("Email is already registered");
        }

        User user = new User(
                request.fullName().trim(),
                email,
                passwordEncoder.encode(request.password()),
                UserRole.PARENT
        );

        try {
            return toResponse(userRepository.save(user));
        } catch (DataIntegrityViolationException exception) {
            throw new DuplicateEmailException("Email is already registered");
        }
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }

    private UserResponse toResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getRole(),
                user.getCreatedAt()
        );
    }
}
