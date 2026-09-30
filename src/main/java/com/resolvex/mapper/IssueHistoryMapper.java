package com.resolvex.mapper;

import org.springframework.stereotype.Component;

import com.resolvex.dto.response.IssueHistoryResponse;
import com.resolvex.entity.IssueHistory;

@Component
public class IssueHistoryMapper {

    public IssueHistoryResponse toResponse(IssueHistory history) {
        IssueHistoryResponse response = new IssueHistoryResponse();

        response.setHistoryId(history.getHistoryId());
        response.setOldStatus(history.getOldStatus());
        response.setNewStatus(history.getNewStatus());
        response.setChangedAt(history.getChangedAt());

        if (history.getIssue() != null) {
            response.setIssueId(history.getIssue().getIssueId());
        }

        if (history.getChangedBy() != null) {
            response.setChangedById(
                    history.getChangedBy().getUserId());
        }

        return response;
    }
}