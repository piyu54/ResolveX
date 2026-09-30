package com.resolvex.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.resolvex.dto.response.DashboardResponse;
import com.resolvex.entity.Status;
import com.resolvex.repository.IssueRepository;
import com.resolvex.service.DashboardService;

@Service
@Transactional(readOnly = true)
public class DashboardServiceImpl implements DashboardService {

    private final IssueRepository issueRepository;

    public DashboardServiceImpl(IssueRepository issueRepository) {
        this.issueRepository = issueRepository;
    }

    @Override
    public DashboardResponse getSummary() {

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
}