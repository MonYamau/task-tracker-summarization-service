package ru.monyamau.task_tracker_summarization_service.exception;

public class UnavailableLlmRequestException extends RuntimeException {
    public UnavailableLlmRequestException(String message, Throwable cause) {
        super(message, cause);
    }
}