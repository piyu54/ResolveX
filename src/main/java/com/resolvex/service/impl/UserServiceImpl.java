package com.resolvex.service.impl;

import java.nio.charset.StandardCharsets;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

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
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final DepartmentRepository departmentRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(
            UserRepository userRepository,
            RoleRepository roleRepository,
            DepartmentRepository departmentRepository,
            UserMapper userMapper,
            PasswordEncoder passwordEncoder) {

        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.departmentRepository = departmentRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserResponse createUser(UserRequest request) {

        validateRequiredFields(request);

        String password = request.getPassword();

        if (password == null || password.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Password is required");
        }

        if (password.length() < 8 || password.length() > 64) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Password must contain 8 to 64 characters");
        }

        // BCrypt limits passwords by encoded bytes.
        if (password.getBytes(StandardCharsets.UTF_8).length > 72) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Password must not exceed 72 UTF-8 bytes");
        }

        String email = request.getEmail().trim();

        requireAvailableEmail(email, null);

        Role role = findRole(request.getRoleId());
        Department department =
                findDepartment(request.getDepartmentId());

        User user = userMapper.toEntity(request);

        user.setFirstName(request.getFirstName().trim());
        user.setLastName(request.getLastName().trim());
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(role);
        user.setDepartment(department);

        User savedUser = userRepository.saveAndFlush(user);

        return userMapper.toResponse(savedUser);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {

        return userRepository.findAll()
                .stream()
                .map(userMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserById(Long userId) {

        return userMapper.toResponse(findUser(userId));
    }

    @Override
    public UserResponse updateUser(
            Long userId,
            UserRequest request) {

        validateRequiredFields(request);

        User user = findUser(userId);
        String email = request.getEmail().trim();

        // The current user's own email is allowed.
        requireAvailableEmail(email, user.getUserId());

        Role role = findRole(request.getRoleId());
        Department department =
                findDepartment(request.getDepartmentId());

        user.setFirstName(request.getFirstName().trim());
        user.setLastName(request.getLastName().trim());
        user.setEmail(email);
        user.setRole(role);
        user.setDepartment(department);

        // Profile updates keep the existing password.
        User updatedUser = userRepository.saveAndFlush(user);

        return userMapper.toResponse(updatedUser);
    }

    @Override
    public void deleteUser(Long userId) {

        User user = findUser(userId);

        userRepository.delete(user);
        userRepository.flush();
    }

    private void requireAvailableEmail(
            String email,
            Long currentUserId) {

        userRepository.findByEmailIgnoreCase(email)
                .ifPresent(existingUser -> {
                    if (!existingUser.getUserId()
                            .equals(currentUserId)) {
                        throw new ResponseStatusException(
                                HttpStatus.CONFLICT,
                                "Email already exists");
                    }
                });
    }

    private User findUser(Long userId) {

        requirePositiveId(userId, "User ID");

        return userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "User not found"));
    }

    private Role findRole(Long roleId) {

        requirePositiveId(roleId, "Role ID");

        return roleRepository.findById(roleId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Role not found"));
    }

    private Department findDepartment(Long departmentId) {

        requirePositiveId(departmentId, "Department ID");

        return departmentRepository.findById(departmentId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Department not found"));
    }

    private void requirePositiveId(Long id, String fieldName) {

        if (id == null || id <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    fieldName + " must be positive");
        }
    }

    private void validateRequiredFields(UserRequest request) {

        if (request == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Request body is required");
        }

        if (request.getFirstName() == null
                || request.getFirstName().isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "First name is required");
        }

        if (request.getLastName() == null
                || request.getLastName().isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Last name is required");
        }

        if (request.getEmail() == null
                || request.getEmail().isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Email is required");
        }
    }
}