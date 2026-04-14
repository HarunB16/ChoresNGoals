package com.choresngoals.entity;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "children")
public class Child {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "parent_id", nullable = false)
    private User parent;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "family_id")
    private Family family;

    @Column(nullable = false, length = 120)
    private String name;

    @Column(nullable = false)
    private Integer birthYear;

    @Column(nullable = false, length = 30)
    private String avatarColor;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    protected Child() {
    }

    public Child(User parent, String name, Integer birthYear, String avatarColor) {
        this(parent, null, name, birthYear, avatarColor);
    }

    public Child(User parent, Family family, String name, Integer birthYear, String avatarColor) {
        this.parent = parent;
        this.family = family;
        this.name = name;
        this.birthYear = birthYear;
        this.avatarColor = avatarColor;
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

    public User getParent() {
        return parent;
    }

    public Family getFamily() {
        return family;
    }

    public void setFamily(Family family) {
        this.family = family;
    }

    public String getName() {
        return name;
    }

    public Integer getBirthYear() {
        return birthYear;
    }

    public String getAvatarColor() {
        return avatarColor;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
