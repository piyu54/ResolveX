package com.resolvex.mapper;

import org.springframework.stereotype.Component;

import com.resolvex.dto.response.AttachmentResponse;
import com.resolvex.entity.Attachment;

@Component
public class AttachmentMapper {

    public AttachmentResponse toResponse(Attachment attachment) {
        AttachmentResponse response = new AttachmentResponse();

        response.setAttachmentId(attachment.getAttachmentId());
        response.setFileName(attachment.getFileName());
        response.setFileType(attachment.getFileType());
        response.setUploadedAt(attachment.getUploadedAt());

        if (attachment.getIssue() != null) {
            response.setIssueId(attachment.getIssue().getIssueId());
        }

        if (attachment.getUploadedBy() != null) {
            response.setUploadedById(
                    attachment.getUploadedBy().getUserId());
        }

        return response;
    }
}