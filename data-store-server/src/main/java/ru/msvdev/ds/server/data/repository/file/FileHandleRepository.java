package ru.msvdev.ds.server.data.repository.file;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.Repository;
import ru.msvdev.ds.server.data.entity.file.FileHandle;

import java.util.List;
import java.util.Optional;


/**
 * Репозиторий для управления хранением глобальных дескрипторов файлов
 */
public interface FileHandleRepository extends Repository<FileHandle, Long> {

    @Query("SELECT * FROM folder_handle")
    Optional<FileHandle> findFolderHandle();


    @Query("SELECT * FROM file_handles WHERE id = :fileHandleId")
    Optional<FileHandle> findById(long fileHandleId);

    @Query("SELECT * FROM file_handles WHERE sha256 = :sha256")
    Optional<FileHandle> findBySha256(String sha256);


    @Query("SELECT id FROM file_handles WHERE sha256 = :sha256")
    Long findIdBySha256(String sha256);

    @Query("SELECT EXISTS(SELECT id FROM file_handles WHERE sha256 = :sha256)")
    boolean existSha256(String sha256);


    @Query("""
            WITH inserted_file_handle AS (
                INSERT INTO file_handles (sha256, mime_type, size, chunk_count, chunk_size, last_chunk_size)
                VALUES (:sha256, :mimeType, :size, :chunkCount, :chunkSize, :lastChunkSize)
                RETURNING id
            )
            SELECT id FROM inserted_file_handle
            """)
    Long insert(String sha256, String mimeType, long size, int chunkCount, int chunkSize, int lastChunkSize);


    @Query("SELECT EXISTS(SELECT * FROM file_chunks WHERE file_handle_id = :fileHandleId AND chunk_id = :chunkId)")
    boolean existsChunk(long fileHandleId, long chunkId);

    @Query("SELECT EXISTS(SELECT * FROM file_chunks WHERE file_handle_id = :fileHandleId AND number = :chunkNumber)")
    boolean existsChunkNumber(long fileHandleId, int chunkNumber);

    @Query("SELECT number FROM file_chunks WHERE file_handle_id = :fileHandleId")
    List<Integer> findChunkNumbers(long fileHandleId);


    @Modifying
    @Query("INSERT INTO file_chunks (file_handle_id, chunk_id, number) VALUES (:fileHandleId, :chunkId, :chunkNumber)")
    boolean insertChunk(long fileHandleId, long chunkId, long chunkNumber);

    @Modifying
    @Query("DELETE FROM file_chunks WHERE file_handle_id = :fileHandleId")
    boolean deleteAllChunks(long fileHandleId);

    @Modifying
    @Query("DELETE FROM file_chunks WHERE file_handle_id = :fileHandleId AND chunk_id = :chunkId")
    boolean deleteChunk(long fileHandleId, long chunkId);

}
