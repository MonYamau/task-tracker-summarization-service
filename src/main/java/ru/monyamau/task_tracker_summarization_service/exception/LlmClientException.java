package ru.monyamau.task_tracker_summarization_service.exception;

public class LlmClientException extends RuntimeException {
    public LlmClientException(String message, Throwable cause) {
        super(message, cause);
    }
}