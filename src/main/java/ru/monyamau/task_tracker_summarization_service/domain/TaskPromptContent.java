package ru.monyamau.task_tracker_summarization_service.domain;

public record TaskPromptContent(String title, String text, boolean isReady) {
}