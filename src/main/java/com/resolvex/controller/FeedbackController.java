package com.resolvex.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.resolvex.dto.request.FeedbackRequest;
import com.resolvex.dto.response.FeedbackResponse;
import com.resolvex.service.FeedbackService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/issues/{issueId}/feedback")
public class FeedbackController {

    private final FeedbackService feedbackService;

    public FeedbackController(FeedbackService feedbackService) {
        this.feedbackService = feedbackService;
    }

    @PostMapping
    public ResponseEntity<FeedbackResponse> createFeedback(
            @PathVariable Long issueId,
            @Valid @RequestBody FeedbackRequest request) {

        FeedbackResponse response =
                feedbackService.createFeedback(issueId, request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping
    public ResponseEntity<FeedbackResponse> getFeedback(
            @PathVariable Long issueId) {

        return ResponseEntity.ok(
                feedbackService.getFeedbackByIssueId(issueId));
    }
}