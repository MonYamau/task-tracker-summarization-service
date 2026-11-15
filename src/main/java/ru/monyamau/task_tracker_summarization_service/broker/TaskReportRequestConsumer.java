package ru.monyamau.task_tracker_summarization_service.broker;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Component;
import ru.monyamau.task_tracker_summarization_service.dto.request.PersonalTaskCollectionRequestDto;
import ru.monyamau.task_tracker_summarization_service.dto.response.PersonalReportResponseDto;
import ru.monyamau.task_tracker_summarization_service.service.SummarizationService;

import java.util.Set;

@Slf4j
@Component
public class TaskReportRequestConsumer {
    private final SummarizationService summarizationService;
    private final Validator validator;

    public TaskReportRequestConsumer(SummarizationService summarizationService, Validator validator) {
        this.summarizationService = summarizationService;
        this.validator = validator;
    }

    @KafkaListener(topics = "${kafka.topics.request}")
    @SendTo
    public PersonalReportResponseDto consume(@Payload PersonalTaskCollectionRequestDto requestDto) {
        try {
            Set<ConstraintViolation<PersonalTaskCollectionRequestDto>> violations = validator.validate(requestDto);
            if (!violations.isEmpty()) {
                log.warn("Запрос на отчёт не прошёл проверку: {}", violations);
                return new PersonalReportResponseDto(null, true);
            }
            return summarizationService.summarize(requestDto);
        } catch (Exception e) {
            log.error("Возникла ошибка при попытке формирования персонального отчёта", e);
            return new PersonalReportResponseDto(null, true);
        }
    }
}