package com.resolvex.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.resolvex.entity.Status;
import java.util.List;

import com.resolvex.entity.Issue;



public interface IssueRepository extends JpaRepository<Issue, Long> {
	
	long countByStatus(Status status);
	

	List<Issue> findByReportedBy_UserIdOrderByCreatedAtDesc(
	        Long userId);

	List<Issue> findByAssignedTo_UserIdOrderByCreatedAtDesc(
	        Long userId);
	
	
}