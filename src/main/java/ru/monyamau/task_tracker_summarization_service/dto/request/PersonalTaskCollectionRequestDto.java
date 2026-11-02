package ru.monyamau.task_tracker_summarization_service.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record PersonalTaskCollectionRequestDto(
        @NotBlank(message = "Электронная почта не может отсутствовать")
        @Email(message = "Невалидный формат электронной почты")
        String userEmail,
        @NotNull(message = "Список задач не может отсутствовать")
        @NotEmpty(message = "Список задач не может быть пустым")
        List<@Valid TaskRequestDto> tasks) {
}