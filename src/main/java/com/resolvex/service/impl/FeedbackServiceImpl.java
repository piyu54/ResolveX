package com.resolvex.service.impl;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.resolvex.dto.request.FeedbackRequest;
import com.resolvex.dto.response.FeedbackResponse;
import com.resolvex.entity.Feedback;
import com.resolvex.entity.Issue;
import com.resolvex.entity.Status;
import com.resolvex.entity.User;
import com.resolvex.mapper.FeedbackMapper;
import com.resolvex.repository.FeedbackRepository;
import com.resolvex.repository.IssueRepository;
import com.resolvex.repository.UserRepository;
import com.resolvex.service.FeedbackService;

@Service
@Transactional
public class FeedbackServiceImpl implements FeedbackService {

    private final FeedbackRepository feedbackRepository;
    private final IssueRepository issueRepository;
    private final UserRepository userRepository;
    private final FeedbackMapper feedbackMapper;

    public FeedbackServiceImpl(
            FeedbackRepository feedbackRepository,
            IssueRepository issueRepository,
            UserRepository userRepository,
            FeedbackMapper feedbackMapper) {

        this.feedbackRepository = feedbackRepository;
        this.issueRepository = issueRepository;
        this.userRepository = userRepository;
        this.feedbackMapper = feedbackMapper;
    }

    @Override
    public FeedbackResponse createFeedback(
            Long issueId,
            FeedbackRequest request) {

        User currentUser = getCurrentUser();

        if (request == null
                || request.getRating() == null
                || request.getRating() < 1
                || request.getRating() > 5) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Rating must be between 1 and 5");
        }

        if (request.getComment() != null
                && request.getComment().length() > 500) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Comment must not exceed 500 characters");
        }

        Issue issue = findIssue(issueId);

        if (!isReporter(issue, currentUser)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Only the issue reporter can submit feedback");
        }

        if (issue.getStatus() != Status.RESOLVED
                && issue.getStatus() != Status.CLOSED) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Feedback is allowed only for resolved or closed issues");
        }

        if (feedbackRepository.existsByIssue_IssueId(issueId)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Feedback already exists for this issue");
        }

        Feedback feedback = feedbackMapper.toEntity(
                request, issue, currentUser);

        Feedback savedFeedback =
                feedbackRepository.saveAndFlush(feedback);

        return feedbackMapper.toResponse(savedFeedback);
    }

    @Override
    @Transactional(readOnly = true)
    public FeedbackResponse getFeedbackByIssueId(Long issueId) {

        User currentUser = getCurrentUser();
        Issue issue = findIssue(issueId);

        boolean isAssignedSupport = hasRole("SUPPORT")
                && issue.getAssignedTo() != null
                && currentUser.getUserId().equals(
                        issue.getAssignedTo().getUserId());

        if (!hasRole("ADMIN")
                && !hasRole("MANAGER")
                && !isReporter(issue, currentUser)
                && !isAssignedSupport) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "You cannot view feedback for this issue");
        }

        Feedback feedback = feedbackRepository
                .findByIssue_IssueId(issueId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Feedback not found for this issue"));

        return feedbackMapper.toResponse(feedback);
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

    private boolean isReporter(Issue issue, User user) {

        return issue.getReportedBy() != null
                && user.getUserId().equals(
                        issue.getReportedBy().getUserId());
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