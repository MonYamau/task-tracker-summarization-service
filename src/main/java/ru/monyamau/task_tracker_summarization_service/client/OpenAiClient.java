package ru.monyamau.task_tracker_summarization_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import ru.monyamau.task_tracker_summarization_service.domain.openAi.OpenAiRequest;
import ru.monyamau.task_tracker_summarization_service.domain.openAi.OpenAiResponse;

@Component
@FeignClient(
        name = "openAiClient",
        url = "${openai.url}"
)
public interface OpenAiClient {
    @PostMapping(value = "${openai.endpoint}",
    consumes = MediaType.APPLICATION_JSON_VALUE,
    produces = MediaType.APPLICATION_JSON_VALUE)
    OpenAiResponse summarize(OpenAiRequest request);
}
