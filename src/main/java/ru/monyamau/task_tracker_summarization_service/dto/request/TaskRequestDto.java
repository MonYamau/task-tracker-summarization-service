package ru.monyamau.task_tracker_summarization_service.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record TaskRequestDto(
        @NotBlank(message = "У задачи не может отсутствовать заголовок")
        @Size(max = 100, message = "Заголовок для задачи объёмом более 100 символов недопустим")
        String title,
        @Size(max = 200, message = "Описание для задачи объёмом более 200 символов недопустимо")
        String text,
        boolean isReady) {
}