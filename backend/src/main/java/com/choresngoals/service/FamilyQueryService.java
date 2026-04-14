package com.choresngoals.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.choresngoals.dto.FamilyResponse;
import com.choresngoals.entity.Family;
import com.choresngoals.entity.User;
import com.choresngoals.entity.UserRole;
import com.choresngoals.repository.UserRepository;
import com.choresngoals.security.AuthenticatedUser;

@Service
public class FamilyQueryService {

    private final UserRepository userRepository;
    private final FamilyStructureService familyStructureService;

    public FamilyQueryService(UserRepository userRepository, FamilyStructureService familyStructureService) {
        this.userRepository = userRepository;
        this.familyStructureService = familyStructureService;
    }

    @Transactional
    public FamilyResponse getCurrentFamily(AuthenticatedUser authenticatedUser) {
        if (authenticatedUser.role() != UserRole.PARENT) {
            throw new ForbiddenOperationException("Only parent accounts can view family details");
        }

        User parent = userRepository.findById(authenticatedUser.id())
                .orElseThrow(() -> new ForbiddenOperationException("Authenticated parent was not found"));
        Family family = familyStructureService.ensureFamilyForParentWithChildren(parent);

        return new FamilyResponse(family.getId(), family.getName(), family.getCreatedAt());
    }
}
