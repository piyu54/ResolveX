package com.resolvex.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.resolvex.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {

}