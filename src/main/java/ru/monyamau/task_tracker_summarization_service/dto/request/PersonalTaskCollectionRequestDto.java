package ru.monyamau.task_tracker_summarization_service.dto.request;

import java.util.List;

public record PersonalTaskCollectionRequestDto(Integer userId, List<TaskRequestDto> tasks) {
}