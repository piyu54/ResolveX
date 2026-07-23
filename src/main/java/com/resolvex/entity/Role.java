package com.resolvex.entity;

import java.util.List;

import jakarta.persistence.*;

@Entity
@Table(name = "roles")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roleId;

    @Column(
        name = "role_name",
        nullable = false,
        unique = true,
        length = 50
    )
    private String roleName;

    @OneToMany(mappedBy = "role")
    private List<User> users;

    // Constructors

    public Role() {
    }

    // Getters and Setters

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }
}