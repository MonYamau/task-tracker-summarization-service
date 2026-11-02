package ru.monyamau.task_tracker_summarization_service.dto.request;

import jakarta.validation.constraints.NotBlank;

public record TaskRequestDto(
        @NotBlank(message = "У задачи не может отсутствовать заголовок")
        String title,
        String text,
        boolean isReady) {
}