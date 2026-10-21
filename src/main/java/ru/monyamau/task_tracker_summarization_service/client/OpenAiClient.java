package ru.monyamau.task_tracker_summarization_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.monyamau.task_tracker_summarization_service.domain.OpenAiRequest;
import ru.monyamau.task_tracker_summarization_service.domain.OpenAiResponse;

@FeignClient(
        name = "openAiClient",
        url = "${llm.url}"
)
public interface OpenAiClient {
    @PostMapping(value = "${llm.endpoint}",
    consumes = MediaType.APPLICATION_JSON_VALUE,
    produces = MediaType.APPLICATION_JSON_VALUE)
    OpenAiResponse createCompletion(@RequestBody OpenAiRequest request);
}
