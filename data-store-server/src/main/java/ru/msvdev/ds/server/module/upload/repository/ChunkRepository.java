package ru.msvdev.ds.server.module.upload.repository;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.Repository;
import ru.msvdev.ds.server.module.upload.entity.Chunk;


/**
 * Репозиторий управления фрагментами данных содержащихся в контейнере
 */
public interface ChunkRepository extends Repository<Chunk, Long> {

    @Query("SELECT content FROM chunks WHERE id = :chunkId")
    String findContent(long chunkId);


    @Query("""
            WITH inserted_chunk AS (
                INSERT INTO chunks (content) VALUES (:content)
                RETURNING id
            )
            SELECT id FROM inserted_chunk
            """)
    Long insert(String content);


    @Modifying
    @Query("UPDATE chunks SET content = :content WHERE id = :chunkId")
    boolean update(long chunkId, String content);

}
