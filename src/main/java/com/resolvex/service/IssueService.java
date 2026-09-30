package com.resolvex.service;

import java.util.List;

import com.resolvex.dto.request.IssueRequest;
import com.resolvex.dto.response.IssueHistoryResponse;
import com.resolvex.dto.response.IssueResponse;
import com.resolvex.entity.Status;

public interface IssueService {

    IssueResponse createIssue(IssueRequest request);

    IssueResponse getIssueById(Long issueId);

    List<IssueResponse> getAllIssues();

    List<IssueResponse> getMyIssues();

    List<IssueResponse> getAssignedToMe();

    IssueResponse assignIssue(
            Long issueId,
            Long assignedToId);

    IssueResponse updateIssueStatus(
            Long issueId,
            Status status,
            Long changedById);

    List<IssueHistoryResponse> getIssueHistory(Long issueId);
}