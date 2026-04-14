package com.choresngoals.service;

import java.time.Year;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.choresngoals.dto.ChildResponse;
import com.choresngoals.dto.CreateChildRequest;
import com.choresngoals.entity.Child;
import com.choresngoals.entity.Family;
import com.choresngoals.entity.User;
import com.choresngoals.entity.UserRole;
import com.choresngoals.repository.ChildRepository;
import com.choresngoals.repository.UserRepository;
import com.choresngoals.security.AuthenticatedUser;

@Service
public class ChildManagementService {

    private final ChildRepository childRepository;
    private final UserRepository userRepository;
    private final FamilyStructureService familyStructureService;

    public ChildManagementService(
            ChildRepository childRepository,
            UserRepository userRepository,
            FamilyStructureService familyStructureService
    ) {
        this.childRepository = childRepository;
        this.userRepository = userRepository;
        this.familyStructureService = familyStructureService;
    }

    @Transactional
    public ChildResponse createChild(AuthenticatedUser authenticatedUser, CreateChildRequest request) {
        ensureParent(authenticatedUser);
        validateBirthYear(request.birthYear());

        User parent = userRepository.findById(authenticatedUser.id())
                .orElseThrow(() -> new ForbiddenOperationException("Authenticated parent was not found"));
        Family family = familyStructureService.ensureFamilyForParent(parent);

        Child child = new Child(
                parent,
                family,
                request.name().trim(),
                request.birthYear(),
                request.avatarColor().trim()
        );

        Child savedChild = childRepository.save(child);
        familyStructureService.attachChildToFamily(family, savedChild);

        return toResponse(savedChild);
    }

    @Transactional
    public List<ChildResponse> listChildren(AuthenticatedUser authenticatedUser) {
        ensureParent(authenticatedUser);
        migrateExistingChildren(authenticatedUser);

        return childRepository.findAllByParentIdOrderByCreatedAtDesc(authenticatedUser.id())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public ChildResponse getChild(AuthenticatedUser authenticatedUser, UUID childId) {
        ensureParent(authenticatedUser);
        migrateExistingChildren(authenticatedUser);

        Child child = childRepository.findByIdAndParentId(childId, authenticatedUser.id())
                .orElseThrow(() -> new ChildNotFoundException("Child was not found"));

        return toResponse(child);
    }

    private void ensureParent(AuthenticatedUser authenticatedUser) {
        if (authenticatedUser.role() != UserRole.PARENT) {
            throw new ForbiddenOperationException("Only parent accounts can manage children");
        }
    }

    private void migrateExistingChildren(AuthenticatedUser authenticatedUser) {
        User parent = userRepository.findById(authenticatedUser.id())
                .orElseThrow(() -> new ForbiddenOperationException("Authenticated parent was not found"));

        familyStructureService.ensureFamilyForParentWithChildren(parent);
    }

    private void validateBirthYear(Integer birthYear) {
        int currentYear = Year.now().getValue();

        if (birthYear > currentYear) {
            throw new IllegalArgumentException("Birth year cannot be in the future");
        }
    }

    private ChildResponse toResponse(Child child) {
        return new ChildResponse(
                child.getId(),
                child.getName(),
                child.getBirthYear(),
                child.getAvatarColor(),
                child.getCreatedAt()
        );
    }
}
