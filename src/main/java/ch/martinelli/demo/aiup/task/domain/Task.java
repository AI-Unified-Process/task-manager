package ch.martinelli.demo.aiup.task.domain;

import java.time.LocalDateTime;

public record Task(Long id, Long teamId, String title, String description, String status, String assignedTo,
		String createdBy, LocalDateTime createdAt, LocalDateTime updatedAt) {

	public String getAssignedToDisplay() {
		return assignedTo != null ? assignedTo : "Unassigned";
	}

}
