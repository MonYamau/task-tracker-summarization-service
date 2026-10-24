package ru.monyamau.task_tracker_summarization_service.service;

import feign.FeignException;
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
import ru.monyamau.task_tracker_summarization_service.util.PromptUtil;

import java.util.List;

@Slf4j
@Service
public class SummarizationService {
    private final OpenAiClient openAiClient;
    private final String model;

    public SummarizationService(OpenAiClient openAiClient, @Value("${llm.model}") String model) {
        this.openAiClient = openAiClient;
        this.model = model;
    }

    public PersonalReportResponseDto summarize(PersonalTaskCollectionRequestDto requestDto) {
        List<TaskPromptContent> convertedTasks = requestDto.tasks()
                .stream()
                .map(this::convert)
                .toList();
        Message constructedPrompt = PromptUtil.constructPrompt(convertedTasks);
        OpenAiResponse clientCompletion = handleCompletionCreating(new OpenAiRequest(model, List.of(constructedPrompt)));
        String contentResponse = clientCompletion.choices().getFirst().message().content();
        return new PersonalReportResponseDto(requestDto.userId(), contentResponse);
    }

    private OpenAiResponse handleCompletionCreating(OpenAiRequest openAiRequest) {
        try {
            return openAiClient.createCompletion(openAiRequest);
        } catch (FeignException e) {
            log.error("Не удалось реализовать отчёт для пользователя при попытке обращения к LLM", e);
            throw new LlmClientException("Возникла ошибка при попытке обращения к LLM API", e);
        }
    }

    private TaskPromptContent convert(TaskRequestDto taskDto) {
        return new TaskPromptContent(taskDto.title(), taskDto.text(), taskDto.isReady());
    }
}