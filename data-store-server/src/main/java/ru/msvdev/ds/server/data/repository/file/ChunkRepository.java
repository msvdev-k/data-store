package ru.msvdev.ds.server.data.repository.file;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.Repository;
import ru.msvdev.ds.server.data.entity.file.Chunk;


/**
 * Репозиторий управления фрагментами данных содержащихся в контейнере
 */
public interface ChunkRepository extends Repository<Chunk, Long> {

    @Query("SELECT size, content, -1 AS number FROM chunks WHERE id = :chunkId")
    Chunk findById(long chunkId);


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
