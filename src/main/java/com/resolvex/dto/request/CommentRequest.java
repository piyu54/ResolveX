package com.resolvex.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public class CommentRequest {

    @NotBlank(message = "Comment message is required")
    @Size(
            max = 1000,
            message = "Comment must not exceed 1000 characters")
    private String message;

    @Positive(message = "User ID must be positive")
    private Long userId;

    public CommentRequest() {
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}