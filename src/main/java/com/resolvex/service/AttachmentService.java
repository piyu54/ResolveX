package com.resolvex.service;

import java.util.List;
import com.resolvex.dto.response.AttachmentDownload;

import org.springframework.web.multipart.MultipartFile;

import com.resolvex.dto.response.AttachmentResponse;

public interface AttachmentService {

    AttachmentResponse uploadAttachment(
            Long issueId,
            Long uploadedById,
            MultipartFile file);

    List<AttachmentResponse> getAttachmentsByIssue(Long issueId);
    
    AttachmentDownload downloadAttachment(
            Long issueId,
            Long attachmentId);
}