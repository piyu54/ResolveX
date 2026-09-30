package com.resolvex.controller;

import java.util.List;
import java.nio.charset.StandardCharsets;

import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import com.resolvex.dto.response.AttachmentDownload;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.resolvex.dto.response.AttachmentResponse;
import com.resolvex.service.AttachmentService;

@RestController
@RequestMapping("/api/issues/{issueId}/attachments")
public class AttachmentController {

    private final AttachmentService attachmentService;

    public AttachmentController(AttachmentService attachmentService) {
        this.attachmentService = attachmentService;
    }

    @PostMapping
    public ResponseEntity<AttachmentResponse> uploadAttachment(
            @PathVariable Long issueId,
            @RequestParam Long uploadedById,
            @RequestParam MultipartFile file) {

        AttachmentResponse response = attachmentService
                .uploadAttachment(issueId, uploadedById, file);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<AttachmentResponse>> getAttachments(
            @PathVariable Long issueId) {

        return ResponseEntity.ok(
                attachmentService.getAttachmentsByIssue(issueId));
    }
    @GetMapping("/{attachmentId}/download")
    public ResponseEntity<Resource> downloadAttachment(
            @PathVariable Long issueId,
            @PathVariable Long attachmentId) {

        AttachmentDownload download = attachmentService
                .downloadAttachment(issueId, attachmentId);

        ContentDisposition disposition = ContentDisposition.attachment()
                .filename(download.fileName(), StandardCharsets.UTF_8)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        disposition.toString())
                .contentType(MediaType.parseMediaType(
                        download.fileType()))
                .body(download.resource());
    }
}