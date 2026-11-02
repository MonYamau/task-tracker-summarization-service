package ru.monyamau.task_tracker_summarization_service.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.TopicPartition;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import ru.monyamau.task_tracker_summarization_service.dto.request.PersonalTaskCollectionRequestDto;

@Configuration
public class KafkaConfig {
    private final String reportResponseTopic;
    private final String dltReportRequestTopic;

    public KafkaConfig(@Value("${kafka.topics.response}") String reportResponseTopic,
                       @Value("${kafka.topics.dlt-request}") String dltReportRequestTopic) {
        this.reportResponseTopic = reportResponseTopic;
        this.dltReportRequestTopic = dltReportRequestTopic;
    }

    @Bean
    public NewTopic createReportResponseTopic() {
        return TopicBuilder.name(reportResponseTopic)
                .replicas(1)
                .partitions(1)
                .build();
    }

    @Bean
    public NewTopic createDltReportRequestTopic() {
        return TopicBuilder.name(dltReportRequestTopic)
                .replicas(1)
                .partitions(1)
                .build();
    }

    @Bean
    public DefaultErrorHandler defaultErrorHandler(KafkaTemplate<String, PersonalTaskCollectionRequestDto> kafkaTemplate) {
        DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(kafkaTemplate,
                (record, ex) -> new TopicPartition(dltReportRequestTopic, record.partition()));
        recoverer.setThrowIfNoDestinationReturned(true);
        recoverer.setLogRecoveryRecord(true);
        recoverer.setFailIfSendResultIsError(true);
        return new DefaultErrorHandler(recoverer);
    }
}