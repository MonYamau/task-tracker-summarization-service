package ru.monyamau.task_tracker_summarization_service.exception;

public class UnexpectedLlmResponseException extends RuntimeException {
    public UnexpectedLlmResponseException(String message) {
        super(message);
    }
}