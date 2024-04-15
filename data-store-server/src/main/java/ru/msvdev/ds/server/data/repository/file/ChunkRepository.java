package ru.msvdev.ds.server.data.repository.file;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.Repository;
import ru.msvdev.ds.server.data.entity.file.FileChunk;

import java.util.Optional;


public interface ChunkRepository extends Repository<FileChunk, Long> {

    @Query("SELECT size, content FROM chunks WHERE id = :chunkId")
    Optional<FileChunk> findById(long chunkId);


    @Query("""
            WITH inserted_chunk AS (
                INSERT INTO chunks (size, content) VALUES (:size, :content)
                RETURNING id
            )
            SELECT id FROM inserted_chunk
            """)
    Long insert(int size, String content);


    @Modifying
    @Query("UPDATE chunks SET content = :content WHERE id = :chunkId")
    boolean updateContent(long chunkId, String content);

    @Modifying
    @Query("DELETE FROM chunks WHERE id = :chunkId")
    boolean deleteById(long chunkId);
}
