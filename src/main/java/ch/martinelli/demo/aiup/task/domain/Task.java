package ch.martinelli.demo.aiup.task.domain;

import org.jspecify.annotations.Nullable;

import java.time.LocalDateTime;

public record Task(Long id, Long teamId, String title, String description, String status, @Nullable String assignedTo,
		String createdBy, LocalDateTime createdAt, LocalDateTime updatedAt) {

	public String getAssignedToDisplay() {
		return assignedTo != null ? assignedTo : "Unassigned";
	}

}
