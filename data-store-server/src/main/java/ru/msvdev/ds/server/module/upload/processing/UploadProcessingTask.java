package ru.msvdev.ds.server.module.upload.processing;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import ru.msvdev.ds.server.module.upload.service.UploadSessionService;

import java.security.NoSuchAlgorithmException;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;


@Component
@RequiredArgsConstructor
public class UploadProcessingTask {

    private final UploadSessionService uploadSessionService;


    private final AtomicInteger runningTaskCounter = new AtomicInteger(0);

    private final BlockingQueue<Long> sessionIdQueue = new LinkedBlockingQueue<>();
    private final Set<Long> usedSessionIdSet = ConcurrentHashMap.newKeySet();


    /**
     * Запуск обработчика пула задач верифицирующих фрагменты выгруженных на сервер файлов.
     * Метод запускается асинхронно и выполняется (блокирует поток выполнения)
     * до полного опустошения общего пула задач. Если задач больше нет, то
     * метод завершает своё действие и освобождает поток
     */
    @Async
    public void runUploadSessionProcessingTask() {
        try {
            runningTaskCounter.incrementAndGet();

            while (true) {
                Long sessionId;

                // Получение текущей задачи. Если задач нет, то завершаем работу.
                // Блок синхронизации исключает случай пропадания сессии из виду,
                // т.е. ситуацию при которой сессия уже удалена из очереди, но
                // ещё не занесена в множество обрабатываемых в текущий момент сессий
                synchronized (usedSessionIdSet) {
                    sessionId = sessionIdQueue.poll();
                    if (sessionId == null) break; // <- Условие выхода из цикла
                    usedSessionIdSet.add(sessionId);
                }

                uploadSessionService.processing(sessionId);

                usedSessionIdSet.remove(sessionId);
                Thread.sleep(1); // <-- Проверка прерывания потока исполнения
            }

        } catch (InterruptedException | NoSuchAlgorithmException e) {
            throw new RuntimeException(e);

        } finally {
            runningTaskCounter.decrementAndGet();
        }
    }


    /**
     * Добавить сессии обработки фрагментов выгруженных на сервер файлов
     * в общий пул задач
     *
     * @param processingSessionIds массив идентификаторов добавляемых сессий
     */
    public void addSessionsToProcess(long[] processingSessionIds) {
        for (long sessionId : processingSessionIds) {
            // Проверка наличия сессии в очереди
            if (sessionIdQueue.contains(sessionId)) continue;

            // Проверка сессии находящейся в процессе обработки.
            // Блок синхронизации исключает случай пропадания сессии из виду,
            // т.е. ситуацию при которой сессия уже удалена из очереди, но
            // ещё не занесена в множество обрабатываемых в текущий момент сессий
            synchronized (usedSessionIdSet) {
                if (usedSessionIdSet.contains(sessionId)) continue;
            }

            // Добавление сессии в очередь
            sessionIdQueue.add(sessionId);
        }
    }


    /**
     * Получить количество параллельно запущенных задач обработки фрагментов
     * выгруженных на сервер файлов
     *
     * @return количество параллельно запущенных задач
     */
    public int getRunningTaskCount() {
        return runningTaskCounter.get();
    }
}
