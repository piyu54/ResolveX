package com.resolvex.entity;

import java.time.LocalDateTime;


import org.hibernate.annotations.CreationTimestamp;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name="issue_history")
public class IssueHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long historyId;

    @Enumerated(EnumType.STRING)
    private Status oldStatus;

    @Enumerated(EnumType.STRING)
    private Status newStatus;

    @CreationTimestamp
    private LocalDateTime changedAt;

    @ManyToOne
    @JoinColumn(name="issue_id")
    private Issue issue;

    @ManyToOne
    @JoinColumn(name="changed_by")
    private User changedBy;
}
