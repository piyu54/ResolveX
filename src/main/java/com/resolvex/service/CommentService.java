package com.resolvex.service;

import java.util.List;

import com.resolvex.dto.request.CommentRequest;
import com.resolvex.dto.response.CommentResponse;

public interface CommentService {

    CommentResponse addComment(Long issueId, CommentRequest request);

    List<CommentResponse> getCommentsByIssue(Long issueId);
}