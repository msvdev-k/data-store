package ru.msvdev.ds.server.config.property;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.unit.DataSize;

import java.time.Duration;

/**
 * Свойства сессии выгрузки содержимого файла на сервер
 *
 * @param uploadChunkTimeout    максимальное время выгрузки фрагмента файла
 * @param uploadSessionTimeout  время существования сессии с момента последнего изменения
 * @param chunkSize             размер фрагмента разбиения на части содержимого файла
 * @param minChunkSize          минимальный размер фрагмента меньше которого разбивка на части не производится
 * @param processingThreadCount количество потоков обрабатывающих корректность полученных от пользователей файлов
 */
@ConfigurationProperties(prefix = "datastore.server.upload-session")
public record UploadSessionProperty(
        Duration uploadChunkTimeout,
        Duration uploadSessionTimeout,
        DataSize chunkSize,
        DataSize minChunkSize,

        int processingThreadCount
) {
}
