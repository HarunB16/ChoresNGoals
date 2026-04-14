package com.choresngoals.service;

import java.util.Locale;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.choresngoals.dto.RegisterParentRequest;
import com.choresngoals.dto.UserResponse;
import com.choresngoals.entity.Family;
import com.choresngoals.entity.FamilyMembership;
import com.choresngoals.entity.FamilyRole;
import com.choresngoals.entity.User;
import com.choresngoals.entity.UserRole;
import com.choresngoals.repository.FamilyMembershipRepository;
import com.choresngoals.repository.FamilyRepository;
import com.choresngoals.repository.UserRepository;

@Service
public class ParentRegistrationService {

    private final UserRepository userRepository;
    private final FamilyRepository familyRepository;
    private final FamilyMembershipRepository familyMembershipRepository;
    private final PasswordEncoder passwordEncoder;

    public ParentRegistrationService(
            UserRepository userRepository,
            FamilyRepository familyRepository,
            FamilyMembershipRepository familyMembershipRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.familyRepository = familyRepository;
        this.familyMembershipRepository = familyMembershipRepository;
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
            User savedUser = userRepository.save(user);
            Family family = familyRepository.save(new Family(savedUser.getFullName() + " Family"));
            familyMembershipRepository.save(new FamilyMembership(family, savedUser, FamilyRole.PARENT));

            return toResponse(savedUser);
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
