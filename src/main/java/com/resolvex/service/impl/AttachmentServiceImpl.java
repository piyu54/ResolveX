package com.resolvex.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.resolvex.dto.response.AttachmentDownload;
import com.resolvex.dto.response.AttachmentResponse;
import com.resolvex.entity.Attachment;
import com.resolvex.entity.Issue;
import com.resolvex.entity.User;
import com.resolvex.mapper.AttachmentMapper;
import com.resolvex.repository.AttachmentRepository;
import com.resolvex.repository.IssueRepository;
import com.resolvex.repository.UserRepository;
import com.resolvex.service.AttachmentService;

@Service
@Transactional
public class AttachmentServiceImpl implements AttachmentService {

    private static final long MAX_FILE_SIZE = 5L * 1024 * 1024;

    private static final Set<String> ALLOWED_TYPES = Set.of(
            "image/jpeg",
            "image/png",
            "application/pdf");

    private final AttachmentRepository attachmentRepository;
    private final IssueRepository issueRepository;
    private final UserRepository userRepository;
    private final AttachmentMapper attachmentMapper;

    private final Path uploadDirectory = Path.of(
            "uploads", "issue-attachments")
            .toAbsolutePath()
            .normalize();

    public AttachmentServiceImpl(
            AttachmentRepository attachmentRepository,
            IssueRepository issueRepository,
            UserRepository userRepository,
            AttachmentMapper attachmentMapper) {

        this.attachmentRepository = attachmentRepository;
        this.issueRepository = issueRepository;
        this.userRepository = userRepository;
        this.attachmentMapper = attachmentMapper;
    }

    @Override
    public AttachmentResponse uploadAttachment(
            Long issueId,
            Long uploadedById,
            MultipartFile file) {

        User uploader = getCurrentUser();

        // Retain the existing method signature.
        // The authenticated user is always the uploader.
        if (uploadedById != null
                && !uploadedById.equals(uploader.getUserId())) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Uploader ID must match the logged-in user");
        }

        Issue issue = findIssue(issueId);
        requireIssueAccess(issue, uploader);

        if (file == null || file.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "File is required");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "File must be 5 MB or smaller");
        }

        String fileType = file.getContentType();

        if (fileType == null || !ALLOWED_TYPES.contains(fileType)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Only JPEG, PNG, and PDF files are allowed");
        }

        String originalName = file.getOriginalFilename();

        String fileName = originalName == null
                ? "upload"
                : originalName.replace('\\', '/');

        fileName = fileName.substring(
                fileName.lastIndexOf('/') + 1);

        if (fileName.isBlank()) {
            fileName = "upload";
        }

        if (fileName.length() > 255
                || fileName.chars().anyMatch(Character::isISOControl)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "File name is invalid or exceeds 255 characters");
        }

        Path target = uploadDirectory
                .resolve(UUID.randomUUID().toString())
                .normalize();

        try {
            Files.createDirectories(uploadDirectory);

            try (InputStream input = file.getInputStream()) {
                Files.copy(input, target);
            }
        } catch (IOException ex) {
            deleteStoredFile(target, ex);

            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Could not store the file",
                    ex);
        }

        Attachment attachment = new Attachment();
        attachment.setFileName(fileName);
        attachment.setFileType(fileType);
        attachment.setFilePath(target.toString());
        attachment.setIssue(issue);
        attachment.setUploadedBy(uploader);

        try {
            Attachment saved =
                    attachmentRepository.saveAndFlush(attachment);

            return attachmentMapper.toResponse(saved);
        } catch (RuntimeException ex) {
            deleteStoredFile(target, ex);
            throw ex;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<AttachmentResponse> getAttachmentsByIssue(
            Long issueId) {

        User currentUser = getCurrentUser();

        Issue issue = findIssue(issueId);
        requireIssueAccess(issue, currentUser);

        return attachmentRepository
                .findByIssue_IssueIdOrderByUploadedAtAsc(issueId)
                .stream()
                .map(attachmentMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public AttachmentDownload downloadAttachment(
            Long issueId,
            Long attachmentId) {

        User currentUser = getCurrentUser();

        Issue issue = findIssue(issueId);
        requireIssueAccess(issue, currentUser);

        if (attachmentId == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Attachment ID is required");
        }

        Attachment attachment = attachmentRepository
                .findById(attachmentId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Attachment not found"));

        if (attachment.getIssue() == null
                || !issueId.equals(
                        attachment.getIssue().getIssueId())) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Attachment not found for this issue");
        }

        if (attachment.getFilePath() == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Stored file not found");
        }

        Path path = Path.of(attachment.getFilePath())
                .toAbsolutePath()
                .normalize();

        if (!path.startsWith(uploadDirectory)
                || !Files.isRegularFile(path)
                || !Files.isReadable(path)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Stored file not found");
        }

        // Resolve symbolic links before allowing the download.
        try {
            Path realDirectory = uploadDirectory.toRealPath();
            Path realPath = path.toRealPath();

            if (!realPath.startsWith(realDirectory)) {
                throw new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Stored file not found");
            }

            Resource resource = new FileSystemResource(realPath);

            return new AttachmentDownload(
                    resource,
                    attachment.getFileName(),
                    attachment.getFileType());

        } catch (IOException ex) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Stored file not found",
                    ex);
        }
    }

    private Issue findIssue(Long issueId) {

        if (issueId == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Issue ID is required");
        }

        return issueRepository.findById(issueId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Issue not found"));
    }

    private void requireIssueAccess(
            Issue issue,
            User currentUser) {

        boolean isReporter = issue.getReportedBy() != null
                && currentUser.getUserId().equals(
                        issue.getReportedBy().getUserId());

        boolean isAssignedSupport = hasRole("SUPPORT")
                && issue.getAssignedTo() != null
                && currentUser.getUserId().equals(
                        issue.getAssignedTo().getUserId());

        if (!hasRole("ADMIN")
                && !hasRole("MANAGER")
                && !isReporter
                && !isAssignedSupport) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "You do not have access to this issue");
        }
    }

    private boolean hasRole(String role) {

        Authentication authentication = SecurityContextHolder
                .getContext()
                .getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()
                || authentication instanceof AnonymousAuthenticationToken) {
            return false;
        }

        return authentication.getAuthorities()
                .stream()
                .anyMatch(authority ->
                        ("ROLE_" + role).equals(
                                authority.getAuthority()));
    }

    private User getCurrentUser() {

        Authentication authentication = SecurityContextHolder
                .getContext()
                .getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()
                || authentication instanceof AnonymousAuthenticationToken) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Login is required");
        }

        return userRepository
                .findByEmailIgnoreCase(authentication.getName())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "Authenticated user no longer exists"));
    }

    private void deleteStoredFile(
            Path path,
            Throwable originalException) {

        try {
            Files.deleteIfExists(path);
        } catch (IOException cleanupException) {
            originalException.addSuppressed(cleanupException);
        }
    }
}