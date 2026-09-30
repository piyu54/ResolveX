package com.resolvex.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.resolvex.entity.Attachment;

public interface AttachmentRepository
        extends JpaRepository<Attachment, Long> {

    List<Attachment> findByIssue_IssueIdOrderByUploadedAtAsc(
            Long issueId);
}