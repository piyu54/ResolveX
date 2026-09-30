package com.resolvex.dto.request;

import com.resolvex.entity.Status;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class UpdateIssueStatusRequest {

    @NotNull(message = "Status is required")
    private Status status;

    @Positive(message = "Changed-by ID must be positive")
    private Long changedById;

    public UpdateIssueStatusRequest() {
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Long getChangedById() {
        return changedById;
    }

    public void setChangedById(Long changedById) {
        this.changedById = changedById;
    }
}