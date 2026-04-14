package com.choresngoals.entity;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(
        name = "family_memberships",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_family_memberships_user_role", columnNames = {"family_id", "user_id", "role"}),
                @UniqueConstraint(name = "uk_family_memberships_child_role", columnNames = {"family_id", "child_id", "role"})
        },
        indexes = {
                @Index(name = "idx_family_memberships_user", columnList = "user_id"),
                @Index(name = "idx_family_memberships_child", columnList = "child_id")
        }
)
public class FamilyMembership {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "family_id", nullable = false)
    private Family family;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "child_id")
    private Child child;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private FamilyRole role;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    protected FamilyMembership() {
    }

    public FamilyMembership(Family family, User user, FamilyRole role) {
        this.family = family;
        this.user = user;
        this.role = role;
    }

    public FamilyMembership(Family family, Child child, FamilyRole role) {
        this.family = family;
        this.child = child;
        this.role = role;
    }

    @PrePersist
    void prePersist() {
        if (createdAt == null) {
            createdAt = Instant.now();
        }
    }

    public UUID getId() {
        return id;
    }

    public Family getFamily() {
        return family;
    }

    public User getUser() {
        return user;
    }

    public Child getChild() {
        return child;
    }

    public FamilyRole getRole() {
        return role;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
