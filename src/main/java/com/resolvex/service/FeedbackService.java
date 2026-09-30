package com.resolvex.service;

import com.resolvex.dto.request.FeedbackRequest;
import com.resolvex.dto.response.FeedbackResponse;

public interface FeedbackService {

    FeedbackResponse createFeedback(Long issueId, FeedbackRequest request);

    FeedbackResponse getFeedbackByIssueId(Long issueId);
}