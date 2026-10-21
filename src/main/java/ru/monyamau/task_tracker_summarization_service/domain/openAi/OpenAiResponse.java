package ru.monyamau.task_tracker_summarization_service.domain.openAi;

import java.util.List;

public record OpenAiResponse(List<Choice> choices) {
}
