package com.resolvex.mapper;

import org.springframework.stereotype.Component;

import com.resolvex.dto.request.CommentRequest;
import com.resolvex.dto.response.CommentResponse;
import com.resolvex.entity.Comment;
import com.resolvex.entity.Issue;
import com.resolvex.entity.User;

@Component
public class CommentMapper {

    public Comment toEntity(
            CommentRequest request,
            Issue issue,
            User user) {

        Comment comment = new Comment();
        comment.setMessage(request.getMessage());
        comment.setIssue(issue);
        comment.setUser(user);

        return comment;
    }

    public CommentResponse toResponse(Comment comment) {
        CommentResponse response = new CommentResponse();

        response.setCommentId(comment.getCommentId());
        response.setMessage(comment.getMessage());
        response.setCommentedAt(comment.getCommentedAt());
        response.setIssueId(comment.getIssue().getIssueId());
        response.setUserId(comment.getUser().getUserId());

        return response;
    }
}