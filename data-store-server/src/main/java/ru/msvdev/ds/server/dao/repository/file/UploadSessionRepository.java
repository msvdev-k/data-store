package ru.msvdev.ds.server.dao.repository.file;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.Repository;
import ru.msvdev.ds.server.dao.entity.file.UploadSession;
import ru.msvdev.ds.server.utils.file.UploadSessionState;

import java.time.OffsetDateTime;
import java.util.UUID;


/**
 * Репозиторий управления сессиями выгрузки файлов на сервер
 */
public interface UploadSessionRepository extends Repository<UploadSession, Long> {

    /**
     * Добавить сессию выгрузки файла
     *
     * @param state         состояние сессии выгрузки файла
     * @param sha256        hash-сумма содержимого файла
     * @param size          размер файла (байт)
     * @param chunkCount    количество фрагментов на которое разбит файл
     * @param chunkSize     размер фрагмента файла (байт)
     * @param lastChunkSize размер последнего фрагмента файла (байт)
     * @return идентификатор сессии выгрузки файла
     */
    @Query("""
            WITH inserted_session AS (
                INSERT INTO upload_sessions (state, sha256, size, chunk_count, chunk_size, last_chunk_size)
                VALUES (:state, :sha256, :size, :chunkCount, :chunkSize, :lastChunkSize)
                RETURNING id
            )
            SELECT id FROM inserted_session
            """)
    Long insert(UploadSessionState state, String sha256, long size, int chunkCount, int chunkSize, int lastChunkSize);


    /**
     * Обновить состояние сессии выгрузки файла
     *
     * @param uploadSessionId идентификатор сессии выгрузки файла
     * @param newState        новое состояние сессии
     * @return True - изменение успешное, False - изменение не произошло
     */
    @Modifying
    @Query("UPDATE upload_sessions SET state = :newState WHERE id = :uploadSessionId")
    boolean updateState(long uploadSessionId, UploadSessionState newState);


    /**
     * Удалить сессию выгрузки файла
     *
     * @param uploadSessionId идентификатор удаляемой сессии
     * @return True - удаление успешное, False - удаление не произошло
     */
    @Modifying
    @Query("DELETE FROM upload_sessions WHERE id = :uploadSessionId")
    boolean delete(long uploadSessionId);


    /**
     * Найти сессию выгрузки файла по её идентификатору
     *
     * @param uploadSessionId идентификатор сессии выгрузки файла
     * @return объект, содержащий сведения о сессии
     */
    @Query("SELECT * FROM upload_sessions WHERE id = :uploadSessionId")
    UploadSession findById(long uploadSessionId);


    /**
     * Найти сессию выгрузки файла по hash-сумме содержимого файла
     *
     * @param sha256 hash-сумма содержимого файла hex(sha256(content))
     * @return объект, содержащий сведения о сессии
     */
    @Query("SELECT * FROM upload_sessions WHERE sha256 = :sha256")
    UploadSession findBySha256(String sha256);


    /**
     * Найти все идентификаторы сессии выгрузки файла по их состоянию
     *
     * @param state состояние сессии выгрузки фрагмента файла
     * @return массив идентификаторов найденных сессий
     */
    @Query("SELECT id FROM upload_sessions WHERE state = :state")
    long[] findAllIdByState(UploadSessionState state);


    /**
     * Добавить сессию выгрузки фрагмента файла
     *
     * @param userUUID        идентификатор пользователя, выгружающего фрагмент файла
     * @param uploadSessionId идентификатор сессии выгрузки файла
     * @param chunkId         идентификатор фрагмента файла
     * @param chunkNumber     порядковый номер фрагмента файла
     * @param state           состояние сессии выгрузки фрагмента файла
     * @param lastModified    дата и время последнего изменения состояния
     * @return True - запись добавлена успешно, False - запись не добавлена
     */
    @Modifying
    @Query("""
            INSERT INTO upload_chunks (upload_session_id, chunk_id, number, user_uuid, state, last_modified)
            VALUES (:uploadSessionId, :chunkId, :chunkNumber, :userUUID, :state, :lastModified)
            """)
    boolean insertUploadChunk(UUID userUUID, long uploadSessionId, long chunkId, int chunkNumber, UploadSessionState state, OffsetDateTime lastModified);


    /**
     * Обновить состояние сессии выгрузки фрагмента файла
     *
     * @param userUUID        идентификатор пользователя, выгружающего фрагмент файла
     * @param uploadSessionId идентификатор сессии выгрузки файла
     * @param chunkNumber     порядковый номер фрагмента файла
     * @param newState        новое состояние сессии выгрузки фрагмента файла
     * @param lastModified    дата и время последнего изменения состояния
     * @return True - состояние изменено успешно, False - изменение не произошло
     */
    @Modifying
    @Query("""
            UPDATE upload_chunks SET state = :newState, last_modified = :lastModified
            WHERE upload_session_id = :uploadSessionId AND number = :chunkNumber AND user_uuid = :userUUID
            """)
    boolean updateUploadChunkState(UUID userUUID, long uploadSessionId, int chunkNumber, UploadSessionState newState, OffsetDateTime lastModified);


    /**
     * Удалить все выгруженные на сервер фрагменты файлов
     * <p>
     * Примечание: метод предназначен для удаления фрагментов файлов
     * ошибочных сессии
     *
     * @param uploadSessionId идентификатор сессии выгрузки файла
     * @return True - удаление успешное, False - удаление не произошло
     */
    @Modifying
    @Query("""
            WITH chunks_for_delete AS (
                DELETE FROM upload_chunks
                WHERE upload_session_id = :uploadSessionId
                RETURNING chunk_id
            )
            DELETE FROM chunks WHERE id IN (SELECT chunk_id FROM chunks_for_delete)
            """)
    boolean deleteUploadChunks(long uploadSessionId);


    /**
     * Удалить все сессии выгрузки фрагментов файлов по их состоянию
     * и дате последнего изменения этого состояния.
     * <p>
     * Примечание: метод предназначен для удаления просроченных сессий
     *
     * @param state        состояния удаляемых сессий
     * @param lastModified дата и время изменения состояния меньше которого сессии удаляются
     * @return True - удаление успешное, False - удаление не произошло
     */
    @Modifying
    @Query("""
            WITH chunks_for_delete AS (
                DELETE FROM upload_chunks
                WHERE state = :state AND last_modified <= :lastModified
                RETURNING chunk_id
            )
            DELETE FROM chunks WHERE id IN (SELECT chunk_id FROM chunks_for_delete)
            """)
    boolean deleteUploadChunks(UploadSessionState state, OffsetDateTime lastModified);


    /**
     * Найти все порядковые номера фрагментов выгружаемого файла
     *
     * @param uploadSessionId идентификатор сессии выгрузки файла
     * @return массив порядковых номеров фрагментов отсортированных по возрастанию
     */
    @Query("SELECT number FROM upload_chunks WHERE upload_session_id = :uploadSessionId ORDER BY number")
    int[] findChunkNumbers(long uploadSessionId);


    /**
     * Получить количество фрагментов выгружаемого файла соответствующих определённому состоянию
     *
     * @param uploadSessionId идентификатор сессии выгрузки файла
     * @param state           состояния искомых фрагментов
     * @return количество фрагментов
     */
    @Query("SELECT COUNT(number) FROM upload_chunks WHERE upload_session_id = :uploadSessionId AND state = :state")
    int countChunkNumbers(long uploadSessionId, UploadSessionState state);


    /**
     * Найти идентификатор фрагмента выгружаемого пользователем файла
     *
     * @param userUUID        идентификатор пользователя
     * @param uploadSessionId идентификатор сессии выгрузки файла
     * @param number          порядковый номер фрагмента
     * @return идентификатор фрагмента выгружаемого файла
     */
    @Query("SELECT chunk_id FROM upload_chunks WHERE upload_session_id = :uploadSessionId AND number = :number AND user_uuid = :userUUID")
    Long findChunkId(UUID userUUID, long uploadSessionId, int number);


    /**
     * Получить содержимое фрагмента файла
     *
     * @param uploadSessionId идентификатор сессии выгрузки файла
     * @param chunkNumber     порядковый номер фрагмента (нумерация начинается с единицы, т.е. 1,2,3,4,...)
     * @return содержимое фрагмента файла в виде строки формата Base64
     */
    @Query("""
            SELECT ch.content
            FROM upload_chunks AS cnt
            INNER JOIN chunks AS ch ON ch.id = cnt.chunk_id
            WHERE cnt.upload_session_id = :uploadSessionId AND cnt.number = :chunkNumber
            """)
    String findChunkContent(long uploadSessionId, int chunkNumber);
}
