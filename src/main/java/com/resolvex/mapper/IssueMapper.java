package com.resolvex.mapper;

import org.springframework.stereotype.Component;

import com.resolvex.dto.request.IssueRequest;
import com.resolvex.dto.response.IssueResponse;
import com.resolvex.entity.Category;
import com.resolvex.entity.Department;
import com.resolvex.entity.Issue;
import com.resolvex.entity.User;

@Component
public class IssueMapper {

    public Issue toEntity(
            IssueRequest request,
            Category category,
            User reportedBy) {

        Issue issue = new Issue();
        issue.setTitle(request.getTitle());
        issue.setDescription(request.getDescription());
        issue.setPriority(request.getPriority());
        issue.setCategory(category);
        issue.setDepartment(category.getDepartment());
        issue.setReportedBy(reportedBy);

        return issue;
    }

    public IssueResponse toResponse(Issue issue) {
        IssueResponse response = new IssueResponse();

        response.setIssueId(issue.getIssueId());
        response.setTitle(issue.getTitle());
        response.setDescription(issue.getDescription());
        response.setStatus(issue.getStatus());
        response.setPriority(issue.getPriority());
        response.setCreatedAt(issue.getCreatedAt());
        response.setUpdatedAt(issue.getUpdatedAt());

        if (issue.getReportedBy() != null) {
            response.setReportedById(issue.getReportedBy().getUserId());
        }

        if (issue.getAssignedTo() != null) {
            response.setAssignedToId(issue.getAssignedTo().getUserId());
        }

        if (issue.getCategory() != null) {
            response.setCategoryId(issue.getCategory().getCategoryId());
            response.setCategoryName(issue.getCategory().getCategoryName());
        }

        Department department = issue.getDepartment();
        if (department != null) {
            response.setDepartmentId(department.getDepartmentId());
            response.setDepartmentName(department.getDepartmentName());
        }

        return response;
    }
}