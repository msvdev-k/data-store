package ru.msvdev.ds.server.module.upload.repository;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.Repository;
import ru.msvdev.ds.server.module.upload.entity.ContainerHeader;


/**
 * Репозиторий для управления контейнерами бинарных данных
 */
public interface ContainerRepository extends Repository<ContainerHeader, Long> {

    /**
     * Получить контейнер по его идентификатору
     *
     * @param containerId идентификатор контейнера
     * @return заголовок контейнера содержащего бинарные данные
     */
    @Query("SELECT * FROM containers WHERE id = :containerId")
    ContainerHeader findById(long containerId);


    /**
     * Получить контейнер по hash-сумме содержащихся в нём данных
     *
     * @param sha256 hash-сумма контейнера данных hex(sha256(content))
     * @return заголовок контейнера содержащего бинарные данные
     */
    @Query("SELECT * FROM containers WHERE sha256 = :sha256")
    ContainerHeader findBySha256(String sha256);


    /**
     * Создать контейнер бинарных данных на основе сессии выгрузки файла на сервер
     *
     * @param uploadSessionId идентификатор сессии выгрузки содержимого файла
     * @return True - контейнер создан успешно, False - контейнер не создан
     */
    @Modifying
    @Query("""
            WITH added_container AS (
                INSERT INTO containers (sha256, size, chunk_count, chunk_size, last_chunk_size)
                SELECT sha256, size, chunk_count, chunk_size, last_chunk_size
                FROM upload_sessions
                WHERE id = :uploadSessionId
                RETURNING id
            )
            INSERT INTO container_chunks (container_id, chunk_id, number)
            SELECT id, chunk_id, number
            FROM added_container, upload_chunks
            WHERE upload_session_id = :uploadSessionId
            """)
    boolean insertFromUploadSession(long uploadSessionId);

}
