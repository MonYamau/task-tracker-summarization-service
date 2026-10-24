package ru.monyamau.task_tracker_summarization_service.config;

import feign.RetryableException;
import feign.Retryer;
import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAiFeignConfig {
    private static final int REQUEST_LIMITING_ERROR = 429;
    private static final int INTERNAL_SERVER_ERROR = 500;
    private static final int BAD_GATEWAY_ERROR = 502;
    private static final int SERVICE_UNAVAILABLE_ERROR = 503;
    private static final int GATEWAY_TIMEOUT_ERROR = 504;

    @Bean
    public ErrorDecoder errorDecoder() {
        ErrorDecoder errorDecoder = new ErrorDecoder.Default();
        return (methodKey, response) -> {
            int status = response.status();
            if (status == REQUEST_LIMITING_ERROR || status == INTERNAL_SERVER_ERROR || status == BAD_GATEWAY_ERROR
                    || status == SERVICE_UNAVAILABLE_ERROR || status == GATEWAY_TIMEOUT_ERROR) {
                return new RetryableException(status, response.reason(), response.request().httpMethod(),
                        (Long) null, response.request());
            }
            return errorDecoder.decode(methodKey, response);
        };
    }

    @Bean
    public Retryer retryer() {
        return new Retryer.Default(1000, 10000, 3);
    }
}