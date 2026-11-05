package ru.monyamau.task_tracker_summarization_service.util;

import lombok.experimental.UtilityClass;
import ru.monyamau.task_tracker_summarization_service.domain.Message;
import ru.monyamau.task_tracker_summarization_service.domain.TaskPromptContent;

import java.util.List;

@UtilityClass
public class PromptUtil {
    private static final String PROMPT_ROLE = "user";
    private static final String BASE_PROMPT = """
            Ты — помощник планировщика задач. Сформируй краткий отчёт на русском языке о выполненных и невыполненных
            за сутки задачах, обращаясь к пользователю на «Вы». Выдай только отчёт без лишнего текста.
            Если массив задач пуст, верни ровно: «За сегодня задач не оказалось. Отдыхайте!»
            Формат отчёта:
            Выполнено задач за день: {кол-во}
            Не выполнено задач за день: {кол-во}
             Правила формирования отчёта:
                    1. Если есть выполненные задачи, добавь раздел «За сегодня удалось:»
                       и перечисли их.
                    2. Если есть невыполненные задачи, добавь раздел «За сегодня не успели:»
                       и перечисли их.
                    3. Если есть хотя бы одна выполненная задача, заверши отчёт фразой:
                       «Вы сегодня отлично потрудились! Не забудьте хорошо отдохнуть!»
                    4. Если выполненных задач нет, но есть невыполненные, заверши отчёт фразой:
                       «Главное, позаботьтесь о себе и своём отдыхе – дела могут подождать!»
            Правила:
            1. Не угадывай время суток выполнения задачи, если его нет в контексте.
            2. Не додумывай задачи и факты. Используй только предоставленные данные.
            3. Избегай избыточных эмоций и ненормативной лексики.
            4. Отчёт должен быть логически согласованным.
            5. Проверяй, чтобы количество перечисленных задач соответствовало указанному количеству.
            6. Не добавляй квадратные скобки, кавычки или другие обрамляющие символы вокруг отчёта.
            """;
    private static final String PERSONAL_PROMPT = """
            Данные: выполненных — %d, невыполненных — %d. Title – заголовок задачи, text – описание задачи,
            isReady – статус выполнения задачи (true – выполнена, false – не выполнена):
            """;

    public static Message constructPrompt(List<TaskPromptContent> tasks,
                                          long countOfCompletedTasks, long countOfUncompletedTasks) {
        return new Message(PROMPT_ROLE, BASE_PROMPT +
                PERSONAL_PROMPT.formatted(countOfCompletedTasks, countOfUncompletedTasks) + tasks.toString());
    }
}