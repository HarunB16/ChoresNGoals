package com.choresngoals.service;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Locale;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.choresngoals.dto.ChildInvitationResponse;
import com.choresngoals.dto.CreateChildInvitationRequest;
import com.choresngoals.entity.ChildInvitation;
import com.choresngoals.entity.Family;
import com.choresngoals.entity.FamilyRole;
import com.choresngoals.entity.UserRole;
import com.choresngoals.repository.ChildInvitationRepository;
import com.choresngoals.repository.FamilyMembershipRepository;
import com.choresngoals.repository.FamilyRepository;
import com.choresngoals.security.AuthenticatedUser;

@Service
public class ChildInvitationService {

    private static final int TOKEN_BYTE_LENGTH = 32;
    private static final int INVITATION_EXPIRY_DAYS = 7;

    private final ChildInvitationRepository childInvitationRepository;
    private final FamilyRepository familyRepository;
    private final FamilyMembershipRepository familyMembershipRepository;
    private final SecureRandom secureRandom = new SecureRandom();

    public ChildInvitationService(
            ChildInvitationRepository childInvitationRepository,
            FamilyRepository familyRepository,
            FamilyMembershipRepository familyMembershipRepository
    ) {
        this.childInvitationRepository = childInvitationRepository;
        this.familyRepository = familyRepository;
        this.familyMembershipRepository = familyMembershipRepository;
    }

    @Transactional
    public ChildInvitationResponse inviteChild(
            AuthenticatedUser authenticatedUser,
            UUID familyId,
            CreateChildInvitationRequest request
    ) {
        ensureParentInFamily(authenticatedUser, familyId);

        Family family = familyRepository.findById(familyId)
                .orElseThrow(() -> new ForbiddenOperationException("Parent is not a member of this family"));
        ChildInvitation invitation = new ChildInvitation(
                family,
                normalizeEmail(request.email()),
                generateUniqueToken(),
                Instant.now().plus(INVITATION_EXPIRY_DAYS, ChronoUnit.DAYS)
        );

        return toResponse(childInvitationRepository.save(invitation));
    }

    private void ensureParentInFamily(AuthenticatedUser authenticatedUser, UUID familyId) {
        if (authenticatedUser.role() != UserRole.PARENT) {
            throw new ForbiddenOperationException("Only parent accounts can invite children");
        }

        boolean parentInFamily = familyMembershipRepository.existsByFamilyIdAndUserIdAndRole(
                familyId,
                authenticatedUser.id(),
                FamilyRole.PARENT
        );

        if (!parentInFamily) {
            throw new ForbiddenOperationException("Parent is not a member of this family");
        }
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }

    private String generateUniqueToken() {
        String token = generateToken();

        while (childInvitationRepository.existsByToken(token)) {
            token = generateToken();
        }

        return token;
    }

    private String generateToken() {
        byte[] bytes = new byte[TOKEN_BYTE_LENGTH];
        secureRandom.nextBytes(bytes);

        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private ChildInvitationResponse toResponse(ChildInvitation invitation) {
        return new ChildInvitationResponse(
                invitation.getId(),
                invitation.getFamily().getId(),
                invitation.getInvitedEmail(),
                invitation.getToken(),
                invitation.getStatus(),
                invitation.getExpiresAt(),
                invitation.getCreatedAt()
        );
    }
}
