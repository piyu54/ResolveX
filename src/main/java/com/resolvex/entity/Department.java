package com.resolvex.entity;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name="departments")
public class Department {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long departmentId;

    @Column(
        name="department_name",
        nullable=false,
        unique=true,
        length=100
    )
    private String departmentName;

    @Column(length=255)
    private String description;

    @OneToMany(mappedBy="department")
    private List<User> users;

}
