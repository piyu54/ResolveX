package com.resolvex.service.impl;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.resolvex.dto.request.IssueRequest;
import com.resolvex.dto.response.IssueHistoryResponse;
import com.resolvex.dto.response.IssueResponse;
import com.resolvex.entity.Category;
import com.resolvex.entity.Issue;
import com.resolvex.entity.IssueHistory;
import com.resolvex.entity.Status;
import com.resolvex.entity.User;
import com.resolvex.mapper.IssueHistoryMapper;
import com.resolvex.mapper.IssueMapper;
import com.resolvex.repository.CategoryRepository;
import com.resolvex.repository.IssueHistoryRepository;
import com.resolvex.repository.IssueRepository;
import com.resolvex.repository.UserRepository;
import com.resolvex.service.IssueService;
import com.resolvex.service.NotificationService;

@Service
@Transactional
public class IssueServiceImpl implements IssueService {

    private final IssueRepository issueRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final IssueHistoryRepository issueHistoryRepository;
    private final IssueMapper issueMapper;
    private final IssueHistoryMapper issueHistoryMapper;
    private final NotificationService notificationService;

    public IssueServiceImpl(
            IssueRepository issueRepository,
            CategoryRepository categoryRepository,
            UserRepository userRepository,
            IssueHistoryRepository issueHistoryRepository,
            IssueMapper issueMapper,
            IssueHistoryMapper issueHistoryMapper,
            NotificationService notificationService) {

        this.issueRepository = issueRepository;
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
        this.issueHistoryRepository = issueHistoryRepository;
        this.issueMapper = issueMapper;
        this.issueHistoryMapper = issueHistoryMapper;
        this.notificationService = notificationService;
    }

    @Override
    public IssueResponse createIssue(IssueRequest request) {

        User reporter = getCurrentUser();

        if (request == null
                || request.getTitle() == null
                || request.getTitle().isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Title is required");
        }

        if (request.getTitle().length() > 255) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Title must not exceed 255 characters");
        }

        if (request.getDescription() != null
                && request.getDescription().length() > 1000) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Description must not exceed 1000 characters");
        }

        if (request.getCategoryId() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Category ID is required");
        }

        Category category = categoryRepository
                .findById(request.getCategoryId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Category not found"));

        if (category.getDepartment() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Assign a department to this category first");
        }

        Issue issue = issueMapper.toEntity(
                request, category, reporter);

        issue.setStatus(Status.OPEN);

        Issue savedIssue = issueRepository.save(issue);

        return issueMapper.toResponse(savedIssue);
    }

    @Override
    @Transactional(readOnly = true)
    public IssueResponse getIssueById(Long issueId) {

        User currentUser = getCurrentUser();
        Issue issue = findIssue(issueId);

        requireReadAccess(issue, currentUser);

        return issueMapper.toResponse(issue);
    }

    @Override
    @Transactional(readOnly = true)
    public List<IssueResponse> getAllIssues() {

        User currentUser = getCurrentUser();

        if (!isAdminOrManager()) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Only admins and managers can list all issues");
        }

        return issueRepository.findAll()
                .stream()
                .map(issueMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<IssueResponse> getMyIssues() {

        User currentUser = getCurrentUser();

        return issueRepository
                .findByReportedBy_UserIdOrderByCreatedAtDesc(
                        currentUser.getUserId())
                .stream()
                .map(issueMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<IssueResponse> getAssignedToMe() {

        User currentUser = getCurrentUser();

        return issueRepository
                .findByAssignedTo_UserIdOrderByCreatedAtDesc(
                        currentUser.getUserId())
                .stream()
                .filter(issue -> canRead(issue, currentUser))
                .map(issueMapper::toResponse)
                .toList();
    }

    @Override
    public IssueResponse assignIssue(
            Long issueId,
            Long assignedToId) {

        getCurrentUser();

        if (!isAdminOrManager()) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Only admins and managers can assign issues");
        }

        if (assignedToId == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Assignee ID is required");
        }

        Issue issue = findIssue(issueId);

        User assignee = userRepository.findById(assignedToId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Assignee not found"));

        if (issue.getAssignedTo() != null
                && assignedToId.equals(
                        issue.getAssignedTo().getUserId())) {
            return issueMapper.toResponse(issue);
        }

        issue.setAssignedTo(assignee);

        Issue savedIssue = issueRepository.save(issue);

        notificationService.createNotification(
                assignee.getUserId(),
                "Issue #" + savedIssue.getIssueId()
                        + " has been assigned to you.");

        return issueMapper.toResponse(savedIssue);
    }

    @Override
    public IssueResponse updateIssueStatus(
            Long issueId,
            Status status,
            Long changedById) {

        User changedBy = getCurrentUser();

        if (!isAdminOrManager() && !hasRole("SUPPORT")) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "You cannot update issue status");
        }

        if (status == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Status is required");
        }

        if (changedById != null
                && !changedById.equals(changedBy.getUserId())) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "changedById must match the logged-in user");
        }

        Issue issue = findIssue(issueId);

        if (!isAdminOrManager()
                && !isAssignee(issue, changedBy)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Support users can update only their assigned issues");
        }

        Status oldStatus = issue.getStatus();

        if (oldStatus == status) {
            return issueMapper.toResponse(issue);
        }

        issue.setStatus(status);

        Issue savedIssue = issueRepository.save(issue);

        IssueHistory history = new IssueHistory();
        history.setIssue(savedIssue);
        history.setOldStatus(oldStatus);
        history.setNewStatus(status);
        history.setChangedBy(changedBy);

        issueHistoryRepository.save(history);

        if (savedIssue.getReportedBy() != null) {
            notificationService.createNotification(
                    savedIssue.getReportedBy().getUserId(),
                    "Issue #" + savedIssue.getIssueId()
                            + " status changed to " + status + ".");
        }

        return issueMapper.toResponse(savedIssue);
    }

    @Override
    @Transactional(readOnly = true)
    public List<IssueHistoryResponse> getIssueHistory(
            Long issueId) {

        User currentUser = getCurrentUser();
        Issue issue = findIssue(issueId);

        requireReadAccess(issue, currentUser);

        return issueHistoryRepository
                .findByIssue_IssueIdOrderByChangedAtAsc(issueId)
                .stream()
                .map(issueHistoryMapper::toResponse)
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

    private void requireReadAccess(
            Issue issue,
            User currentUser) {

        if (!canRead(issue, currentUser)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "You do not have access to this issue");
        }
    }

    private boolean canRead(Issue issue, User currentUser) {

        return isAdminOrManager()
                || isReporter(issue, currentUser)
                || (hasRole("SUPPORT")
                        && isAssignee(issue, currentUser));
    }

    private boolean isReporter(Issue issue, User user) {

        return issue.getReportedBy() != null
                && user.getUserId().equals(
                        issue.getReportedBy().getUserId());
    }

    private boolean isAssignee(Issue issue, User user) {

        return issue.getAssignedTo() != null
                && user.getUserId().equals(
                        issue.getAssignedTo().getUserId());
    }

    private boolean isAdminOrManager() {

        return hasRole("ADMIN") || hasRole("MANAGER");
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