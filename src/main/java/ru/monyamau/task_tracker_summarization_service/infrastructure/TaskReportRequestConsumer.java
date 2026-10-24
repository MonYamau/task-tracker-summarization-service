package ru.monyamau.task_tracker_summarization_service.infrastructure;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.monyamau.task_tracker_summarization_service.dto.request.PersonalTaskCollectionRequestDto;
import ru.monyamau.task_tracker_summarization_service.dto.response.PersonalReportResponseDto;
import ru.monyamau.task_tracker_summarization_service.service.SummarizationService;

@Slf4j
@Component
public class TaskReportRequestConsumer {
    private final SummarizationService summarizationService;
    private final TaskReportResponseProducer responseProducer;

    public TaskReportRequestConsumer(SummarizationService summarizationService, TaskReportResponseProducer responseProducer) {
        this.summarizationService = summarizationService;
        this.responseProducer = responseProducer;
    }

    @KafkaListener(topics = "${kafka.topics.request}")
    public void consume(PersonalTaskCollectionRequestDto requestDto) {
        log.info("Успешно получена задача на реализацию отчёта для пользователя с ID {}", requestDto.userId());
        PersonalReportResponseDto responseDto = summarizationService.summarize(requestDto);
        responseProducer.publishReport(responseDto);
    }
}