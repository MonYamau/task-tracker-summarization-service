package ru.monyamau.task_tracker_summarization_service.exception;

public class TaskReportPublisherException extends RuntimeException {
    public TaskReportPublisherException(String message, Throwable cause) {
        super(message, cause);
    }
}