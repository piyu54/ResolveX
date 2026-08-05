package com.resolvex.service.impl;

import org.springframework.stereotype.Service;

import com.resolvex.repository.RoleRepository;
import com.resolvex.service.RoleService;

import jakarta.persistence.Persistence;

@Service
public class RoleServiceImpl implements RoleService{
	
	private final RoleRepository roleRepository;
	
	 public RoleServiceImpl(RoleRepository roleRepository) {
	        this.roleRepository = roleRepository;
	    }
	 
	 Persistence p ;

}
