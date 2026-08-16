package com.resolvex.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.resolvex.dto.request.UserRequest;
import com.resolvex.dto.response.UserResponse;
import com.resolvex.entity.Department;
import com.resolvex.entity.Role;
import com.resolvex.entity.User;
import com.resolvex.mapper.UserMapper;
import com.resolvex.repository.DepartmentRepository;
import com.resolvex.repository.RoleRepository;
import com.resolvex.repository.UserRepository;
import com.resolvex.service.UserService;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final DepartmentRepository departmentRepository;
    private final UserMapper userMapper;

    // Constructor Injection
    public UserServiceImpl(
            UserRepository userRepository,
            RoleRepository roleRepository,
            DepartmentRepository departmentRepository,
            UserMapper userMapper) {

        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.departmentRepository = departmentRepository;
        this.userMapper = userMapper;
    }

    // CREATE USER
    @Override
    public UserResponse createUser(UserRequest request) {

        User user = userMapper.toEntity(request);

        Role role = roleRepository.findById(request.getRoleId())
                .orElseThrow(() ->
                        new RuntimeException(
                                "Role not found with id: "
                                        + request.getRoleId()));

        Department department = departmentRepository
                .findById(request.getDepartmentId())
                .orElseThrow(() ->
                        new RuntimeException(
                                "Department not found with id: "
                                        + request.getDepartmentId()));

        user.setRole(role);
        user.setDepartment(department);

        User savedUser = userRepository.save(user);

        return userMapper.toResponse(savedUser);
    }

    // GET ALL USERS
    @Override
    public List<UserResponse> getAllUsers() {

        List<User> users = userRepository.findAll();

        return users.stream()
                .map(userMapper::toResponse)
                .collect(Collectors.toList());
    }

    // GET USER BY ID
    @Override
    public UserResponse getUserById(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "User not found with id: " + userId));

        return userMapper.toResponse(user);
    }

    // UPDATE USER
    @Override
    public UserResponse updateUser(
            Long userId,
            UserRequest request) {

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "User not found with id: " + userId));

        Role role = roleRepository.findById(request.getRoleId())
                .orElseThrow(() ->
                        new RuntimeException(
                                "Role not found with id: "
                                        + request.getRoleId()));

        Department department = departmentRepository
                .findById(request.getDepartmentId())
                .orElseThrow(() ->
                        new RuntimeException(
                                "Department not found with id: "
                                        + request.getDepartmentId()));

        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setRole(role);
        user.setDepartment(department);

        User updatedUser = userRepository.save(user);

        return userMapper.toResponse(updatedUser);
    }

    // DELETE USER
    @Override
    public void deleteUser(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "User not found with id: " + userId));

        userRepository.delete(user);
    }
}