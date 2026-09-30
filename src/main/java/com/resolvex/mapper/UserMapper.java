package com.resolvex.mapper;

import org.springframework.stereotype.Component;

import com.resolvex.dto.request.UserRequest;
import com.resolvex.dto.response.UserResponse;
import com.resolvex.entity.User;

@Component
public class UserMapper {

    // UserRequest → User entity
    public User toEntity(UserRequest request) {
        User user = new User();

        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());

        // Password is encoded and set in UserServiceImpl.
        return user;
    }

    // User entity → API response
    public UserResponse toResponse(User user) {
        UserResponse response = new UserResponse();

        response.setUserId(user.getUserId());
        response.setFirstName(user.getFirstName());
        response.setLastName(user.getLastName());
        response.setEmail(user.getEmail());

        if (user.getRole() != null) {
            response.setRoleId(user.getRole().getRoleId());
            response.setRoleName(user.getRole().getRoleName());
        }

        if (user.getDepartment() != null) {
            response.setDepartmentId(
                    user.getDepartment().getDepartmentId());
            response.setDepartmentName(
                    user.getDepartment().getDepartmentName());
        }

        return response;
    }
}