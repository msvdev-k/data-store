package ru.msvdev.ds.server.service.file;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.msvdev.ds.server.property.UploadSessionProperty;

import java.util.concurrent.TimeUnit;


@Component
@RequiredArgsConstructor
public class UploadSessionProcessingScheduler {

    private final UploadSessionProperty property;

    private final UploadSessionService uploadSessionService;
    private final UploadSessionProcessingTask uploadSessionProcessingTask;


    /**
     * Удаление просроченных сессий выгрузки фрагментов файлов на сервер
     */
    @Scheduled(initialDelay = 300, fixedDelay = 30, timeUnit = TimeUnit.SECONDS)
    public void deleteObsoleteUploadChunkSessionsTask() {
        uploadSessionService.deleteObsoleteUploadChunkSessions();
    }


    /**
     * Формирование задач обработки выгруженного на сервер
     * содержимого файлов
     */
    @Scheduled(initialDelay = 5, fixedDelay = 1, timeUnit = TimeUnit.SECONDS)
    public void uploadSessionProcessingTask() {

        // Поиск сессий находящихся в состоянии обработки контента
        long[] processingSessionIds = uploadSessionService.findAllProcessingSessionId();
        if (processingSessionIds.length == 0) return;

        // Добавление сессий в общий пул задач
        uploadSessionProcessingTask.addSessionsToProcess(processingSessionIds);

        // Запуск задач для обработки пула (задачи запускаются в параллельных потоках)
        for (int i = uploadSessionProcessingTask.getRunningTaskCount(); i < property.processingThreadCount(); i++) {
            uploadSessionProcessingTask.runUploadSessionProcessingTask();
        }
    }

}
