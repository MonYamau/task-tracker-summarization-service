package ru.monyamau.task_tracker_summarization_service.infrastructure;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import ru.monyamau.task_tracker_summarization_service.dto.response.PersonalReportResponseDto;
import ru.monyamau.task_tracker_summarization_service.exception.TaskReportPublisherException;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Component
public class TaskReportResponseProducer {
    private final KafkaTemplate<String, PersonalReportResponseDto> kafkaTemplate;
    private final String topic;

    public TaskReportResponseProducer(KafkaTemplate<String, PersonalReportResponseDto> kafkaTemplate, @Value("${kafka.topics.response}") String topic) {
        this.kafkaTemplate = kafkaTemplate;
        this.topic = topic;
    }

    public void publishReport(PersonalReportResponseDto responseDto) {
        try {
            kafkaTemplate.send(topic, responseDto).get(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new TaskReportPublisherException("Попытка публикации задачи в Kafka была прервана", e);
        } catch (TimeoutException e) {
            throw new TaskReportPublisherException("Не удалось получить подтверждение от Kafka о получении задачи", e);
        } catch (ExecutionException e) {
            Throwable cause = e.getCause();
            throw new TaskReportPublisherException("Не удалось опубликовать задачу в Kafka", cause);
        } catch (Exception e) {
            throw new TaskReportPublisherException("Ошибка при попытке опубликовать задачу в Kafka", e);
        }
    }
}