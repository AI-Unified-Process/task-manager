package ch.martinelli.demo.aiup.team.domain;

import java.time.LocalDateTime;

public record Team(Long id, String name, String description, LocalDateTime createdAt, Boolean isActive) {

}
