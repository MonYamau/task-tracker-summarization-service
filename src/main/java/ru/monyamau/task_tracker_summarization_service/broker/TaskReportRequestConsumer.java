package ru.monyamau.task_tracker_summarization_service.broker;

import jakarta.validation.Valid;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Component;
import ru.monyamau.task_tracker_summarization_service.dto.request.PersonalTaskCollectionRequestDto;
import ru.monyamau.task_tracker_summarization_service.dto.response.PersonalReportResponseDto;
import ru.monyamau.task_tracker_summarization_service.service.SummarizationService;

@Component
public class TaskReportRequestConsumer {
    private final SummarizationService summarizationService;

    public TaskReportRequestConsumer(SummarizationService summarizationService) {
        this.summarizationService = summarizationService;
    }

    @KafkaListener(topics = "${kafka.topics.request}")
    @SendTo
    public PersonalReportResponseDto consume(@Valid @Payload PersonalTaskCollectionRequestDto requestDto) {
        return summarizationService.summarize(requestDto);
    }
}