package com.resolvex.service.impl;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.resolvex.dto.response.DashboardResponse;
import com.resolvex.entity.Status;
import com.resolvex.repository.IssueRepository;
import com.resolvex.repository.UserRepository;
import com.resolvex.service.DashboardService;

@Service
@Transactional(readOnly = true)
public class DashboardServiceImpl implements DashboardService {

    private final IssueRepository issueRepository;
    private final UserRepository userRepository;

    public DashboardServiceImpl(
            IssueRepository issueRepository,
            UserRepository userRepository) {

        this.issueRepository = issueRepository;
        this.userRepository = userRepository;
    }

    @Override
    public DashboardResponse getSummary() {

        requireDashboardAccess();

        DashboardResponse response = new DashboardResponse();

        response.setTotalIssues(issueRepository.count());

        response.setOpenIssues(
                issueRepository.countByStatus(Status.OPEN));

        response.setInProgressIssues(
                issueRepository.countByStatus(Status.IN_PROGRESS));

        response.setResolvedIssues(
                issueRepository.countByStatus(Status.RESOLVED));

        response.setClosedIssues(
                issueRepository.countByStatus(Status.CLOSED));

        return response;
    }

    private void requireDashboardAccess() {

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

        userRepository.findByEmailIgnoreCase(authentication.getName())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "Authenticated user no longer exists"));

        boolean allowed = authentication.getAuthorities()
                .stream()
                .anyMatch(authority ->
                        "ROLE_ADMIN".equals(authority.getAuthority())
                        || "ROLE_MANAGER".equals(authority.getAuthority()));

        if (!allowed) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Only admins and managers can view the dashboard");
        }
    }
}