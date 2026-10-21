package ru.monyamau.task_tracker_summarization_service.dto.request;

import java.time.OffsetDateTime;

public record TaskRequestDto(Integer id, String title, String text, boolean isReady, OffsetDateTime completedAt) {
}