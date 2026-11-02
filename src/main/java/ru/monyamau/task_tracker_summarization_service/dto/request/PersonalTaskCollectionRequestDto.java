package ru.monyamau.task_tracker_summarization_service.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.util.List;

public record PersonalTaskCollectionRequestDto(
        @NotBlank(message = "Электронная почта не может отсутствовать")
        @Email(message = "Невалидный формат электронной почты")
        String userEmail,
        @NotNull(message = "Список задач не может отсутствовать")
        @NotEmpty(message = "Список задач не может быть пустым")
        @Size(max = 30, message = "Список в количестве более 30 задач недопустим")
        List<@Valid TaskRequestDto> tasks) {
}