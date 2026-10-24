package ru.monyamau.task_tracker_summarization_service.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;
import ru.monyamau.task_tracker_summarization_service.exception.LlmClientException;

@Configuration
public class KafkaConfig {
    private final String reportResponseTopic;

    public KafkaConfig(@Value("${kafka.topics.response}") String reportResponseTopic) {
        this.reportResponseTopic = reportResponseTopic;
    }

    @Bean
    public NewTopic createReportResponseTopic() {
        return TopicBuilder.name(reportResponseTopic)
                .replicas(1)
                .partitions(1)
                .build();
    }

    @Bean
    public DefaultErrorHandler defaultErrorHandler() {
        DefaultErrorHandler errorHandler = new DefaultErrorHandler(new FixedBackOff(3000, 2));
        errorHandler.addRetryableExceptions(LlmClientException.class);
        return errorHandler;
    }
}