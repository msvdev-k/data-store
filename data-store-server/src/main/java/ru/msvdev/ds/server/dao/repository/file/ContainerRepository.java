package ru.msvdev.ds.server.dao.repository.file;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.Repository;
import ru.msvdev.ds.server.dao.entity.file.Chunk;
import ru.msvdev.ds.server.dao.entity.file.ContainerHeader;


/**
 * Репозиторий для управления контейнерами бинарных данных
 */
public interface ContainerRepository extends Repository<ContainerHeader, Long> {

    /**
     * Получить пустой контейнер
     *
     * @return заголовок контейнера содержащего бинарные данные
     */
    @Query("SELECT * FROM empty_container")
    ContainerHeader findEmptyContainer();


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
     * Получить идентификатор контейнера по hash-сумме содержащихся в нём данных
     *
     * @param sha256 hash-сумма контейнера данных hex(sha256(content))
     * @return идентификатор контейнера
     */
    @Query("SELECT id FROM containers WHERE sha256 = :sha256")
    Long findIdBySha256(String sha256);


    /**
     * Проверить существование контейнера с данными по hash-сумме
     *
     * @param sha256 hash-сумма контейнера данных hex(sha256(content))
     * @return True - контейнер существует, False - не существует
     */
    @Query("SELECT EXISTS(SELECT id FROM containers WHERE sha256 = :sha256)")
    boolean existSha256(String sha256);


    /**
     * Вставить заголовок контейнера
     *
     * @param sha256        hash-сумма контейнера данных hex(sha256(content))
     * @param size          размер контейнера данных (байт)
     * @param chunkCount    количество фрагментов, на которое разбит контейнер
     * @param chunkSize     размер фрагментов контейнера (байт)
     * @param lastChunkSize размер последнего фрагмента контейнера (байт)
     * @return идентификатор вставленного контейнера
     */
    @Query("""
            WITH inserted_container AS (
                INSERT INTO containers (sha256, size, chunk_count, chunk_size, last_chunk_size)
                VALUES (:sha256, :size, :chunkCount, :chunkSize, :lastChunkSize)
                RETURNING id
            )
            SELECT id FROM inserted_container
            """)
    Long insert(String sha256, long size, int chunkCount, int chunkSize, int lastChunkSize);


    /**
     * Создать связь контейнера с фрагментом данных
     *
     * @param containerId идентификатор контейнера
     * @param chunkId     идентификатор фрагмента данных
     * @param chunkNumber порядковый номер фрагмента данных (нумерация начинается с единицы, т.е. 1,2,3,4,...)
     * @return True - вставка произошла успешно, False - вставка не произошла
     */
    @Modifying
    @Query("INSERT INTO container_chunks (container_id, chunk_id, number) VALUES (:containerId, :chunkId, :chunkNumber)")
    boolean insertChunk(long containerId, long chunkId, long chunkNumber);


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


    /**
     * Получить фрагмент данных содержащихся в контейнере
     *
     * @param containerId идентификатор контейнера
     * @param chunkNumber порядковый номер фрагмента (нумерация начинается с единицы, т.е. 1,2,3,4,...)
     * @return фрагмент бинарных данных содержащихся в контейнере
     */
    @Query("""
            SELECT ch.size, ch.content, cnt.number
            FROM container_chunks AS cnt
            INNER JOIN chunks AS ch ON ch.id = cnt.chunk_id
            WHERE cnt.container_id = :containerId AND cnt.number = :chunkNumber
            """)
    Chunk findChunk(long containerId, int chunkNumber);


    /**
     * Полностью удалить контейнер с бинарными данными
     *
     * @param containerId идентификатор контейнера
     * @return True - удаление прошло успешно, False - удаления не происходило
     */
    @Modifying
    @Query("""
            WITH chunks_for_delete AS (
                SELECT ch.id
                FROM container_chunks AS cnt
                INNER JOIN chunks AS ch ON ch.id = cnt.chunk_id
                WHERE cnt.container_id = :containerId
            ), delete_chunks AS (
                DELETE FROM chunks WHERE id IN (SELECT id FROM chunks_for_delete)
            ), delete_container_chunks AS (
                DELETE FROM container_chunks WHERE container_id = :containerId
            )
            DELETE FROM containers WHERE id = :containerId
            """)
    boolean deleteById(long containerId);
}
