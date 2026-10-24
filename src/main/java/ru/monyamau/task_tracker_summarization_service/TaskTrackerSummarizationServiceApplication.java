package ru.monyamau.task_tracker_summarization_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class TaskTrackerSummarizationServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(TaskTrackerSummarizationServiceApplication.class, args);
    }
}