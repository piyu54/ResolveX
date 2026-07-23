package com.resolvex.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name="roles")

public class Role {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long roleId;
	
	@Column(name="role_name",
			nullable=false,
			unique=true,
			length=50)
	private String roleName;
	
	
	@ManyToOne
	@JoinColumn(name = "role_id")
	private Role role;
	
}
