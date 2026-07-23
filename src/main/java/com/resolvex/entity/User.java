package com.resolvex.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(
        name = "first_name",
        nullable = false,
        length = 50
    )
    private String firstName;

    @Column(
        name = "last_name",
        nullable = false,
        length = 50
    )
    private String lastName;

    @Column(
        name = "email",
        nullable = false,
        unique = true,
        length = 100
    )
    private String email;

    @ManyToOne
    @JoinColumn(name = "role_id")
    private Role role;

    // Constructors

    public User() {
    }

    // Getters and Setters

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}