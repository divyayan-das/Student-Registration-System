package com.university.records.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class AppUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(name = "password_hash", nullable = false)
    private String password;

    @Column(name = "role_name", nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;

    private Long linkedEntityId;

    public AppUser() {
    }

    public AppUser(String username, String password, Role role, Long linkedEntityId) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.linkedEntityId = linkedEntityId;
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public Role getRole() {
        return role;
    }

    public Long getLinkedEntityId() {
        return linkedEntityId;
    }
}
