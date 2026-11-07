package ru.monyamau.task_tracker_summarization_service.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.kafka.autoconfigure.ConcurrentKafkaListenerContainerFactoryConfigurer;
import org.springframework.boot.kafka.autoconfigure.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.DelegatingByTypeSerializer;
import org.springframework.kafka.support.serializer.JacksonJsonSerializer;
import org.springframework.util.backoff.FixedBackOff;
import ru.monyamau.task_tracker_summarization_service.dto.request.PersonalTaskCollectionRequestDto;
import ru.monyamau.task_tracker_summarization_service.dto.response.PersonalReportResponseDto;

import java.util.Map;

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
    public ProducerFactory<String, PersonalReportResponseDto> responseProducerFactory(KafkaProperties kafkaProperties) {
        return new DefaultKafkaProducerFactory<>(kafkaProperties.buildProducerProperties());
    }

    @Bean
    public KafkaTemplate<String, PersonalReportResponseDto> responseKafkaTemplate(
            @Qualifier("responseProducerFactory") ProducerFactory<String, PersonalReportResponseDto> producerFactory) {
        return new KafkaTemplate<>(producerFactory);
    }

    @Bean(name = "kafkaListenerContainerFactory")
    public ConcurrentKafkaListenerContainerFactory<Object, Object> kafkaListenerContainerFactory(
            ConcurrentKafkaListenerContainerFactoryConfigurer configurer,
            ConsumerFactory<Object, Object> consumerFactory,
            @Qualifier("responseKafkaTemplate") KafkaTemplate<String, PersonalReportResponseDto> responseKafkaTemplate,
            @Qualifier("defaultErrorHandler") DefaultErrorHandler defaultErrorHandler) {
        ConcurrentKafkaListenerContainerFactory<Object, Object> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        configurer.configure(factory, consumerFactory);
        factory.setReplyTemplate(responseKafkaTemplate);
        factory.setCommonErrorHandler(defaultErrorHandler);
        return factory;
    }

    @Bean
    public ProducerFactory<String, Object> dltProducerFactory(KafkaProperties kafkaProperties) {
        Map<String, Object> producerProperties = kafkaProperties.buildProducerProperties();
        DelegatingByTypeSerializer typeSerializer = new DelegatingByTypeSerializer(
                Map.of(
                        byte[].class, new ByteArraySerializer(),
                        PersonalTaskCollectionRequestDto.class, new JacksonJsonSerializer<>()
                )
        );
        return new DefaultKafkaProducerFactory<>(producerProperties, new StringSerializer(), typeSerializer);
    }

    @Bean
    public KafkaTemplate<String, Object> dltKafkaTemplate(
            @Qualifier("dltProducerFactory") ProducerFactory<String, Object> dltProducerFactory) {
        return new KafkaTemplate<>(dltProducerFactory);
    }

    @Bean
    public DefaultErrorHandler defaultErrorHandler(
            @Qualifier("dltKafkaTemplate") KafkaTemplate<String, Object> kafkaTemplate) {
        DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(kafkaTemplate,
                (record, ex) -> new TopicPartition(dltReportRequestTopic, record.partition()));
        recoverer.setThrowIfNoDestinationReturned(true);
        recoverer.setLogRecoveryRecord(true);
        recoverer.setFailIfSendResultIsError(true);
        return new DefaultErrorHandler(recoverer, new FixedBackOff(0L, 0L));
    }
}