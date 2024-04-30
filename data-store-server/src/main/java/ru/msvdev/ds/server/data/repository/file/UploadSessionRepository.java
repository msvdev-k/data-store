package ru.msvdev.ds.server.data.repository.file;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.Repository;
import ru.msvdev.ds.server.data.entity.file.UploadSession;
import ru.msvdev.ds.server.utils.file.UploadFileState;


public interface UploadSessionRepository extends Repository<UploadSession, Long> {

    @Query("""
            WITH inserted_session AS (
                INSERT INTO upload_sessions (state, sha256, size, chunk_count, chunk_size, last_chunk_size)
                VALUES (:state, :sha256, :size, :chunkCount, :chunkSize, :lastChunkSize)
                RETURNING id
            )
            SELECT id FROM inserted_session
            """)
    Long insert(UploadFileState state, String sha256, long size, int chunkCount, int chunkSize, int lastChunkSize);


    @Modifying
    @Query("UPDATE upload_sessions SET state = :newState WHERE id = :uploadSessionId")
    boolean updateState(long uploadSessionId, UploadFileState newState);


    @Modifying
    @Query("DELETE FROM upload_sessions WHERE id = :uploadSessionId")
    boolean delete(long uploadSessionId);


    @Query("SELECT * FROM upload_sessions WHERE id = :uploadSessionId")
    UploadSession findById(long uploadSessionId);


    @Query("SELECT * FROM upload_sessions WHERE sha256 = :sha256")
    UploadSession findBySha256(String sha256);

}
