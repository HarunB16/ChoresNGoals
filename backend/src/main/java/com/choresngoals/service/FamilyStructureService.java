package com.choresngoals.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.choresngoals.entity.Child;
import com.choresngoals.entity.Family;
import com.choresngoals.entity.FamilyMembership;
import com.choresngoals.entity.FamilyRole;
import com.choresngoals.entity.User;
import com.choresngoals.repository.ChildRepository;
import com.choresngoals.repository.FamilyMembershipRepository;
import com.choresngoals.repository.FamilyRepository;

@Service
public class FamilyStructureService {

    private final FamilyRepository familyRepository;
    private final FamilyMembershipRepository familyMembershipRepository;
    private final ChildRepository childRepository;

    public FamilyStructureService(
            FamilyRepository familyRepository,
            FamilyMembershipRepository familyMembershipRepository,
            ChildRepository childRepository
    ) {
        this.familyRepository = familyRepository;
        this.familyMembershipRepository = familyMembershipRepository;
        this.childRepository = childRepository;
    }

    @Transactional
    public Family ensureFamilyForParent(User parent) {
        return familyMembershipRepository.findFirstByUserIdAndRole(parent.getId(), FamilyRole.PARENT)
                .map(FamilyMembership::getFamily)
                .orElseGet(() -> createFamilyForParent(parent));
    }

    @Transactional
    public Family ensureFamilyForParentWithChildren(User parent) {
        Family family = ensureFamilyForParent(parent);
        List<Child> children = childRepository.findAllByParentIdOrderByCreatedAtDesc(parent.getId());

        for (Child child : children) {
            attachChildToFamily(family, child);
        }

        return family;
    }

    @Transactional
    public void attachChildToFamily(Family family, Child child) {
        if (child.getFamily() == null) {
            child.setFamily(family);
        }

        if (!familyMembershipRepository.existsByFamilyIdAndChildIdAndRole(family.getId(), child.getId(), FamilyRole.CHILD)) {
            familyMembershipRepository.save(new FamilyMembership(family, child, FamilyRole.CHILD));
        }
    }

    private Family createFamilyForParent(User parent) {
        Family family = familyRepository.save(new Family(defaultFamilyName(parent)));

        if (!familyMembershipRepository.existsByFamilyIdAndUserIdAndRole(family.getId(), parent.getId(), FamilyRole.PARENT)) {
            familyMembershipRepository.save(new FamilyMembership(family, parent, FamilyRole.PARENT));
        }

        return family;
    }

    private String defaultFamilyName(User parent) {
        String fullName = parent.getFullName();

        if (fullName == null || fullName.isBlank()) {
            return "Family";
        }

        return fullName.trim() + " Family";
    }
}
