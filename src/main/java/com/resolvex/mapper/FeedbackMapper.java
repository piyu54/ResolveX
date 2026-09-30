package com.resolvex.mapper;

import org.springframework.stereotype.Component;

import com.resolvex.dto.request.FeedbackRequest;
import com.resolvex.dto.response.FeedbackResponse;
import com.resolvex.entity.Feedback;
import com.resolvex.entity.Issue;
import com.resolvex.entity.User;

@Component
public class FeedbackMapper {

    public Feedback toEntity(
            FeedbackRequest request,
            Issue issue,
            User user) {

        Feedback feedback = new Feedback();
        feedback.setRating(request.getRating());
        feedback.setComment(request.getComment());
        feedback.setIssue(issue);
        feedback.setUser(user);

        return feedback;
    }

    public FeedbackResponse toResponse(Feedback feedback) {
        FeedbackResponse response = new FeedbackResponse();

        response.setFeedbackId(feedback.getFeedbackId());
        response.setRating(feedback.getRating());
        response.setComment(feedback.getComment());
        response.setSubmittedAt(feedback.getSubmittedAt());

        if (feedback.getIssue() != null) {
            response.setIssueId(feedback.getIssue().getIssueId());
        }

        if (feedback.getUser() != null) {
            response.setUserId(feedback.getUser().getUserId());
        }

        return response;
    }
}