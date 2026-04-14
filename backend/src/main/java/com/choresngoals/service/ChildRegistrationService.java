package com.choresngoals.service;

import java.time.Instant;
import java.util.Locale;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.choresngoals.dto.RegisterChildRequest;
import com.choresngoals.dto.UserResponse;
import com.choresngoals.dto.ValidateChildInvitationResponse;
import com.choresngoals.entity.ChildInvitation;
import com.choresngoals.entity.ChildInvitationStatus;
import com.choresngoals.entity.FamilyMembership;
import com.choresngoals.entity.FamilyRole;
import com.choresngoals.entity.User;
import com.choresngoals.entity.UserRole;
import com.choresngoals.repository.ChildInvitationRepository;
import com.choresngoals.repository.FamilyMembershipRepository;
import com.choresngoals.repository.UserRepository;

@Service
public class ChildRegistrationService {

    private final ChildInvitationRepository childInvitationRepository;
    private final UserRepository userRepository;
    private final FamilyMembershipRepository familyMembershipRepository;
    private final PasswordEncoder passwordEncoder;

    public ChildRegistrationService(
            ChildInvitationRepository childInvitationRepository,
            UserRepository userRepository,
            FamilyMembershipRepository familyMembershipRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.childInvitationRepository = childInvitationRepository;
        this.userRepository = userRepository;
        this.familyMembershipRepository = familyMembershipRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public ValidateChildInvitationResponse validateInvitation(String token) {
        ChildInvitation invitation = getUsableInvitation(token);

        return new ValidateChildInvitationResponse(
                invitation.getId(),
                invitation.getFamily().getId(),
                invitation.getInvitedEmail(),
                invitation.getStatus(),
                invitation.getExpiresAt(),
                invitation.getCreatedAt()
        );
    }

    @Transactional
    public UserResponse registerChild(RegisterChildRequest request) {
        ChildInvitation invitation = getUsableInvitation(request.invitationToken());
        String email = normalizeEmail(request.email());

        if (!email.equals(invitation.getInvitedEmail())) {
            throw new IllegalArgumentException("Email must match the invited email");
        }

        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw new DuplicateEmailException("Email is already registered");
        }

        User childUser = new User(
                request.fullName().trim(),
                email,
                passwordEncoder.encode(request.password()),
                UserRole.CHILD
        );

        try {
            User savedUser = userRepository.save(childUser);
            familyMembershipRepository.save(new FamilyMembership(invitation.getFamily(), savedUser, FamilyRole.CHILD));
            invitation.markUsed();

            return toResponse(savedUser);
        } catch (DataIntegrityViolationException exception) {
            throw new DuplicateEmailException("Email is already registered");
        }
    }

    private ChildInvitation getUsableInvitation(String token) {
        ChildInvitation invitation = childInvitationRepository.findByToken(token.trim())
                .orElseThrow(() -> new IllegalArgumentException("Invitation token is invalid"));

        if (invitation.getStatus() != ChildInvitationStatus.PENDING) {
            throw new IllegalArgumentException("Invitation has already been used");
        }

        if (invitation.getExpiresAt().isBefore(Instant.now())) {
            throw new IllegalArgumentException("Invitation has expired");
        }

        return invitation;
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
