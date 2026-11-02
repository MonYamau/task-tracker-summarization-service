package ru.monyamau.task_tracker_summarization_service.service;

import feign.FeignException;
import feign.RetryableException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.monyamau.task_tracker_summarization_service.client.OpenAiClient;
import ru.monyamau.task_tracker_summarization_service.domain.Message;
import ru.monyamau.task_tracker_summarization_service.domain.OpenAiRequest;
import ru.monyamau.task_tracker_summarization_service.domain.OpenAiResponse;
import ru.monyamau.task_tracker_summarization_service.domain.TaskPromptContent;
import ru.monyamau.task_tracker_summarization_service.dto.request.PersonalTaskCollectionRequestDto;
import ru.monyamau.task_tracker_summarization_service.dto.request.TaskRequestDto;
import ru.monyamau.task_tracker_summarization_service.dto.response.PersonalReportResponseDto;
import ru.monyamau.task_tracker_summarization_service.exception.LlmClientException;
import ru.monyamau.task_tracker_summarization_service.exception.UnavailableLlmRequestException;
import ru.monyamau.task_tracker_summarization_service.exception.UnexpectedLlmResponseException;
import ru.monyamau.task_tracker_summarization_service.util.PromptUtil;

import java.util.List;

@Slf4j
@Service
public class SummarizationService {
    private static final int MAX_CHAR_COUNT = 2500;

    private final OpenAiClient openAiClient;
    private final String model;

    public SummarizationService(OpenAiClient openAiClient, @Value("${llm.model}") String model) {
        this.openAiClient = openAiClient;
        this.model = model;
    }

    public PersonalReportResponseDto summarize(PersonalTaskCollectionRequestDto requestDto) {
        long countOfUncompletedTasks = countTasksByStatus(false, requestDto.tasks());
        long countOfCompletedTasks = countTasksByStatus(true, requestDto.tasks());
        List<TaskPromptContent> convertedTasks = requestDto.tasks().stream()
                .map(this::convert)
                .toList();
        validateCountOfCharacters(convertedTasks);
        Message constructedPrompt = PromptUtil
                .constructPrompt(convertedTasks, countOfCompletedTasks, countOfUncompletedTasks);
        OpenAiResponse clientCompletion = handleCompletionCreating(new OpenAiRequest(model, List.of(constructedPrompt)));
        String contentResponse = clientCompletion.choices().getFirst().message().content();
        return new PersonalReportResponseDto(requestDto.userEmail(), contentResponse);
    }

    private OpenAiResponse handleCompletionCreating(OpenAiRequest openAiRequest) {
        OpenAiResponse completion;
        try {
            completion = openAiClient.createCompletion(openAiRequest);
        } catch (RetryableException e) {
            throw new UnavailableLlmRequestException("LLM недоступна для сетевых запросов", e);
        } catch (FeignException e) {
            throw new LlmClientException("Возникла ошибка при попытке обращения к LLM API", e);
        }
        if (completion == null || completion.choices() == null || completion.choices().isEmpty()
                || completion.choices().getFirst().message() == null
                || completion.choices().getFirst().message().content() == null
                || completion.choices().getFirst().message().content().isBlank()) {
            throw new UnexpectedLlmResponseException("Не удалось получить корректный ответ от LLM ресурса");
        }
        return completion;
    }

    private void validateCountOfCharacters(List<TaskPromptContent> tasks) {
        if (tasks.toString().length() > MAX_CHAR_COUNT) {
            throw new IllegalArgumentException("Слишком большой объём символов у текущего списка задач");
        }
    }

    private long countTasksByStatus(boolean isReady, List<TaskRequestDto> tasks) {
        return tasks.stream()
                .filter(task -> task.isReady() == isReady)
                .count();
    }

    private TaskPromptContent convert(TaskRequestDto taskDto) {
        return new TaskPromptContent(taskDto.title(), taskDto.text(), taskDto.isReady());
    }
}