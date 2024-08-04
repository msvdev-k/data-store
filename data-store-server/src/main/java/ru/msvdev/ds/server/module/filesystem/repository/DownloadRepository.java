package ru.msvdev.ds.server.module.filesystem.repository;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.Repository;
import ru.msvdev.ds.server.module.filesystem.entity.FileChunk;

/**
 * Репозиторий для получения содержимого файла
 */
public interface DownloadRepository extends Repository<FileChunk, Long> {

    /**
     * Найти фрагмент файла
     *
     * @param catalogId   идентификатор каталога
     * @param nodeId      идентификатор файла
     * @param chunkNumber порядковый номен фрагмента
     * @return описание содержимого фрагмента файла
     */
    @Query("""
            SELECT c.sha256, c.size, c.chunk_count, c.chunk_size, c.last_chunk_size, cch.number, ch.content
            FROM containers AS c
            INNER JOIN files            AS f   ON f.container_id = c.id
            INNER JOIN container_chunks AS cch ON cch.container_id = c.id
            INNER JOIN chunks           AS ch  ON ch.id = cch.chunk_id
            WHERE f.id = :nodeId AND f.catalog_id = :catalogId AND cch.number = :chunkNumber
            """)
    FileChunk findChunk(long catalogId, long nodeId, int chunkNumber);
}
