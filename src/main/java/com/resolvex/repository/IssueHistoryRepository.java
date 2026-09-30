package com.resolvex.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.resolvex.entity.IssueHistory;

public interface IssueHistoryRepository
        extends JpaRepository<IssueHistory, Long> {

    List<IssueHistory> findByIssue_IssueIdOrderByChangedAtAsc(
            Long issueId);
}