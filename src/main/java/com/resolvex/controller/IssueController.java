package com.resolvex.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.resolvex.dto.request.AssignIssueRequest;
import com.resolvex.dto.request.IssueRequest;
import com.resolvex.dto.request.UpdateIssueStatusRequest;
import com.resolvex.dto.response.IssueHistoryResponse;
import com.resolvex.dto.response.IssueResponse;
import com.resolvex.service.IssueService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/issues")
public class IssueController {

    private final IssueService issueService;

    public IssueController(IssueService issueService) {
        this.issueService = issueService;
    }

    @PostMapping
    public ResponseEntity<IssueResponse> createIssue(
            @Valid @RequestBody IssueRequest request) {

        IssueResponse response = issueService.createIssue(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/{issueId}")
    public ResponseEntity<IssueResponse> getIssueById(
            @PathVariable Long issueId) {

        return ResponseEntity.ok(
                issueService.getIssueById(issueId));
    }

    @GetMapping
    public ResponseEntity<List<IssueResponse>> getAllIssues() {

        return ResponseEntity.ok(
                issueService.getAllIssues());
    }

    @GetMapping("/my")
    public ResponseEntity<List<IssueResponse>> getMyIssues() {

        return ResponseEntity.ok(
                issueService.getMyIssues());
    }

    @GetMapping("/assigned-to-me")
    public ResponseEntity<List<IssueResponse>> getAssignedToMe() {

        return ResponseEntity.ok(
                issueService.getAssignedToMe());
    }

    @PatchMapping("/{issueId}/assign")
    public ResponseEntity<IssueResponse> assignIssue(
            @PathVariable Long issueId,
            @Valid @RequestBody AssignIssueRequest request) {

        IssueResponse response = issueService.assignIssue(
                issueId,
                request.getAssignedToId());

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{issueId}/status")
    public ResponseEntity<IssueResponse> updateIssueStatus(
            @PathVariable Long issueId,
            @Valid @RequestBody UpdateIssueStatusRequest request) {

        IssueResponse response = issueService.updateIssueStatus(
                issueId,
                request.getStatus(),
                request.getChangedById());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{issueId}/history")
    public ResponseEntity<List<IssueHistoryResponse>> getIssueHistory(
            @PathVariable Long issueId) {

        return ResponseEntity.ok(
                issueService.getIssueHistory(issueId));
    }
}