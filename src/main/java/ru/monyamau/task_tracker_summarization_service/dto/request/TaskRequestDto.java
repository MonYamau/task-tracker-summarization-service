package ru.monyamau.task_tracker_summarization_service.dto.request;

public record TaskRequestDto(String title, String text, boolean isReady) {
}