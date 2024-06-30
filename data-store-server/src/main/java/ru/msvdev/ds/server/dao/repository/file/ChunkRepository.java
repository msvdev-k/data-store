package ru.msvdev.ds.server.dao.repository.file;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.Repository;
import ru.msvdev.ds.server.dao.entity.file.Chunk;


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
    Long insertContent(String content);


    @Modifying
    @Query("UPDATE chunks SET content = :content WHERE id = :chunkId")
    boolean updateContent(long chunkId, String content);

}
