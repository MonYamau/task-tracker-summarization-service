package ru.monyamau.task_tracker_summarization_service.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record PersonalTaskCollectionRequestDto(
        @NotNull(message = "Список задач не может отсутствовать")
        @NotEmpty(message = "Список задач не может быть пустым")
        @Size(max = 30, message = "Список в количестве более 30 задач недопустим")
        List<@Valid TaskRequestDto> tasks) {
}