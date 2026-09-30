package com.resolvex.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.resolvex.entity.Comment;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findByIssue_IssueIdOrderByCommentedAtAsc(Long issueId);
}