package com.resolvex.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.resolvex.entity.Feedback;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {

    boolean existsByIssue_IssueId(Long issueId);

    Optional<Feedback> findByIssue_IssueId(Long issueId);
}