package ru.msvdev.ds.server.data.repository.file;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.Repository;
import ru.msvdev.ds.server.data.entity.file.UploadSession;
import ru.msvdev.ds.server.utils.file.UploadFileState;

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
    Long insert(UploadFileState state, String sha256, long size, int chunkCount, int chunkSize, int lastChunkSize);


    /**
     * Обновить состояние сессии выгрузки файла
     *
     * @param uploadSessionId идентификатор сессии выгрузки файла
     * @param newState        новое состояние сессии
     * @return True - изменение успешное, False - изменение не произошло
     */
    @Modifying
    @Query("UPDATE upload_sessions SET state = :newState WHERE id = :uploadSessionId")
    boolean updateState(long uploadSessionId, UploadFileState newState);


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
     * Добавить сессию выгрузки фрагмента файла
     *
     * @param userUUID        идентификатор пользователя, выгружающего фрагмент файла
     * @param uploadSessionId идентификатор сессии выгрузки файла
     * @param chunkId         идентификатор фрагмента файла
     * @param number          порядковый номер фрагмента файла
     * @param state           состояние сессии выгрузки фрагмента файла
     * @param lastModified    дата и время последнего изменения состояния
     * @return True - запись добавлена успешно, False - запись не добавлена
     */
    @Modifying
    @Query("""
            INSERT INTO upload_chunks (upload_session_id, chunk_id, number, user_uuid, state, last_modified)
            VALUES (:uploadSessionId, :chunkId, :number, :userUUID, :state, :lastModified)
            """)
    boolean insertUploadChunk(UUID userUUID, long uploadSessionId, long chunkId, int number, UploadFileState state, OffsetDateTime lastModified);


    /**
     * Обновить состояние сессии выгрузки фрагмента файла
     *
     * @param userUUID        идентификатор пользователя, выгружающего фрагмент файла
     * @param uploadSessionId идентификатор сессии выгрузки файла
     * @param number          порядковый номер фрагмента файла
     * @param newState        новое состояние сессии выгрузки фрагмента файла
     * @param lastModified    дата и время последнего изменения состояния
     * @return True - состояние изменено успешно, False - изменение не произошло
     */
    @Modifying
    @Query("""
            UPDATE upload_chunks SET state = :newState, last_modified = :lastModified
            WHERE upload_session_id = :uploadSessionId AND number = :number AND user_uuid = :userUUID
            """)
    boolean updateUploadChunkState(UUID userUUID, long uploadSessionId, int number, UploadFileState newState, OffsetDateTime lastModified);


    /**
     * Удалить сессию выгрузки фрагмента файла по его порядковому номеру
     *
     * @param uploadSessionId идентификатор сессии выгрузки файла
     * @param number          порядковый номер фрагмента файла
     * @return True - удаление успешное, False - удаление не произошло
     */
    @Modifying
    @Query("DELETE FROM upload_chunks WHERE upload_session_id = :uploadSessionId AND number = :number")
    boolean deleteUploadChunk(long uploadSessionId, int number);


    /**
     * Удалить все сессии выгрузки фрагментов файлов по их состоянию
     * и дате последнего изменения этого состояния.
     * <p>
     * Примечание: метод предназначен для удаления просроченных сессий
     *
     * @param uploadSessionId идентификатор сессии выгрузки файла
     * @param state           состояния удаляемых сессий
     * @param lastModified    дата и время изменения состояния меньше которого сессии удаляются
     * @return True - удаление успешное, False - удаление не произошло
     */
    @Modifying
    @Query("DELETE FROM upload_chunks WHERE upload_session_id = :uploadSessionId AND state = :state AND last_modified <= :lastModified")
    boolean deleteUploadChunk(long uploadSessionId, UploadFileState state, OffsetDateTime lastModified);


    /**
     * Найти все порядковые номера фрагментов выгружаемого файла
     *
     * @param uploadSessionId идентификатор сессии выгрузки файла
     * @return массив порядковых номеров фрагментов отсортированных в порядке возрастания
     */
    @Query("SELECT number FROM upload_chunks WHERE upload_session_id = :uploadSessionId ORDER BY number")
    int[] findChunkNumbers(long uploadSessionId);


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
}
