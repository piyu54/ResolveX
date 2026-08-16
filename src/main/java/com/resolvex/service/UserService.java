package com.resolvex.service;

import java.util.List;

import com.resolvex.dto.request.UserRequest;
import com.resolvex.dto.response.UserResponse;

public interface UserService {

    UserResponse createUser(UserRequest request);

    List<UserResponse> getAllUsers();

    UserResponse getUserById(Long userId);

    UserResponse updateUser(Long userId, UserRequest request);

    void deleteUser(Long userId);


    
}