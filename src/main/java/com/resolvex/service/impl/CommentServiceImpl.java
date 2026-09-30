package com.resolvex.service.impl;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.resolvex.dto.request.CommentRequest;
import com.resolvex.dto.response.CommentResponse;
import com.resolvex.entity.Comment;
import com.resolvex.entity.Issue;
import com.resolvex.entity.User;
import com.resolvex.mapper.CommentMapper;
import com.resolvex.repository.CommentRepository;
import com.resolvex.repository.IssueRepository;
import com.resolvex.repository.UserRepository;
import com.resolvex.service.CommentService;

@Service
@Transactional
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final IssueRepository issueRepository;
    private final UserRepository userRepository;
    private final CommentMapper commentMapper;

    public CommentServiceImpl(
            CommentRepository commentRepository,
            IssueRepository issueRepository,
            UserRepository userRepository,
            CommentMapper commentMapper) {

        this.commentRepository = commentRepository;
        this.issueRepository = issueRepository;
        this.userRepository = userRepository;
        this.commentMapper = commentMapper;
    }

    @Override
    public CommentResponse addComment(
            Long issueId,
            CommentRequest request) {

        User currentUser = getCurrentUser();

        if (request == null
                || request.getMessage() == null
                || request.getMessage().isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Comment message is required");
        }

        if (request.getMessage().length() > 1000) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Comment message must not exceed 1000 characters");
        }

        // Keep compatibility with the existing request DTO.
        // The author always comes from the authenticated user.
        if (request.getUserId() != null
                && !request.getUserId().equals(
                        currentUser.getUserId())) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "User ID must match the logged-in user");
        }

        Issue issue = findIssue(issueId);
        requireIssueAccess(issue, currentUser);

        Comment comment = commentMapper.toEntity(
                request, issue, currentUser);

        // Explicitly enforce the authenticated author.
        comment.setUser(currentUser);

        Comment savedComment = commentRepository.save(comment);

        return commentMapper.toResponse(savedComment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommentResponse> getCommentsByIssue(
            Long issueId) {

        User currentUser = getCurrentUser();

        Issue issue = findIssue(issueId);
        requireIssueAccess(issue, currentUser);

        return commentRepository
                .findByIssue_IssueIdOrderByCommentedAtAsc(issueId)
                .stream()
                .map(commentMapper::toResponse)
                .toList();
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
}