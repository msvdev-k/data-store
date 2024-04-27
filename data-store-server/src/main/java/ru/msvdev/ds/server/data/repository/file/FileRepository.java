package ru.msvdev.ds.server.data.repository.file;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.Repository;
import ru.msvdev.ds.server.data.entity.file.ChunkingSchema;
import ru.msvdev.ds.server.data.entity.file.FileInfo;

import java.time.OffsetDateTime;
import java.util.List;


public interface FileRepository extends Repository<FileInfo, Long> {


    /**
     * Создать корневую директорию файловой системы картотеки.
     * Примечание: метод вызывается всего один раз при создании картотеки
     *
     * @param catalogId  идентификатор картотеки
     * @param createDate дата и время создания корневой директории
     * @return TRUE - корневая директория создана, FALSE - не создана
     */
    @Modifying
    @Query("""
            INSERT INTO files (catalog_id, file_handle_id, name, create_date)
            VALUES (:catalogId, (SELECT id FROM folder_handle), 'ROOT', :createDate)
            """)
    boolean insertRoot(long catalogId, OffsetDateTime createDate);


    /**
     * Создать директорию в корне файловой системы картотеки
     *
     * @param catalogId  идентификатор картотеки
     * @param name       название директории
     * @param createDate дата и время создания директории
     * @return идентификатор созданной директории
     */
    @Query("""
            WITH root_id AS (
                SELECT id FROM files
                WHERE catalog_id = :catalogId AND folder_id IS NULL AND uname = 'ROOT'
            ), inserted_folder AS (
                INSERT INTO files (catalog_id, folder_id, file_handle_id, name, create_date)
                VALUES (:catalogId, (SELECT id FROM root_id), (SELECT id FROM folder_handle), :name, :createDate)
                RETURNING id
            )
            SELECT id FROM inserted_folder
            """)
    Long insertToRoot(long catalogId, String name, OffsetDateTime createDate);


    /**
     * Создать файл в корне файловой системы картотеки
     *
     * @param catalogId    идентификатор картотеки
     * @param fileHandleId идентификатор дескриптора файла
     * @param name         название файла
     * @param createDate   дата и время создания файла
     * @return идентификатор созданного файла
     */
    @Query("""
            WITH root_id AS (
                SELECT id FROM files
                WHERE catalog_id = :catalogId AND folder_id IS NULL AND uname = 'ROOT'
            ), inserted_file AS (
                INSERT INTO files (catalog_id, folder_id, file_handle_id, name, create_date)
                VALUES (:catalogId, (SELECT id FROM root_id), :fileHandleId, :name, :createDate)
                RETURNING id
            )
            SELECT id FROM inserted_file
            """)
    Long insertToRoot(long catalogId, long fileHandleId, String name, OffsetDateTime createDate);


    /**
     * Создать каталог в файловой системы картотеки
     *
     * @param catalogId  идентификатор картотеки
     * @param folderId   идентификатор родительского каталога
     * @param name       название создаваемого каталога
     * @param createDate дата и время создания каталога
     * @return идентификатор созданного каталога
     */
    @Query("""
            WITH folder_id AS (
                SELECT id FROM files
                WHERE id = :folderId AND catalog_id = :catalogId
            ), inserted_folder AS (
                INSERT INTO files (catalog_id, folder_id, file_handle_id, name, create_date)
                VALUES (:catalogId, (SELECT id FROM folder_id), (SELECT id FROM folder_handle), :name, :createDate)
                RETURNING id
            )
            SELECT id FROM inserted_folder
            """)
    Long insertToFolder(long catalogId, long folderId, String name, OffsetDateTime createDate);


    /**
     * Создать файл в файловой системы картотеки
     *
     * @param catalogId    идентификатор картотеки
     * @param folderId     идентификатор родительского каталога
     * @param fileHandleId глобальный идентификатор файла
     * @param name         название создаваемого файла
     * @param createDate   дата и время создания файла
     * @return идентификатор созданного файла
     */
    @Query("""
            WITH folder_id AS (
                SELECT id FROM files
                WHERE id = :folderId AND catalog_id = :catalogId
            ), inserted_file AS (
                INSERT INTO files (catalog_id, folder_id, file_handle_id, name, create_date)
                VALUES (:catalogId, (SELECT id FROM folder_id), :fileHandleId, :name, :createDate)
                RETURNING id
            )
            SELECT id FROM inserted_file
            """)
    Long insertToFolder(long catalogId, long folderId, long fileHandleId, String name, OffsetDateTime createDate);


    /**
     * Получить информацию о файле/каталоге по его ID
     *
     * @param catalogId идентификатор картотеки
     * @param fileId    идентификатор файла/каталога
     * @return объект с информацией о файле/каталоге
     */
    @Query("""
            SELECT f.id, COALESCE(f.folder_id, -1) AS folder_id, f.name, fh.mime_type, f.create_date, fh.size
            FROM files AS f
            INNER JOIN file_handles AS fh ON fh.id = f.file_handle_id
            WHERE f.id = :fileId AND f.catalog_id = :catalogId
            """)
    FileInfo findById(long catalogId, long fileId);


    /**
     * Получить hash-сумму файла по его ID
     *
     * @param catalogId идентификатор картотеки
     * @param fileId    идентификатор файла
     * @return hash-сумма файла (sha256)
     */
    @Query("""
            SELECT fh.sha256
            FROM files AS f
            INNER JOIN file_handles AS fh ON fh.id = f.file_handle_id
            WHERE f.id = :fileId AND f.catalog_id = :catalogId
            """)
    String findSha256ById(long catalogId, long fileId);


    /**
     * Получить схему разбиения файла по его ID
     *
     * @param catalogId идентификатор картотеки
     * @param fileId    идентификатор файла
     * @return объект представляющий схему разбиения файла
     */
    @Query("""
            SELECT fh.size, fh.chunk_count AS "count", fh.chunk_size, fh.last_chunk_size
            FROM files AS f
            INNER JOIN file_handles AS fh ON fh.id = f.file_handle_id
            WHERE f.id = :fileId AND f.catalog_id = :catalogId
            """)
    ChunkingSchema findChunkingSchemaById(long catalogId, long fileId);


    /**
     * Получить список файлов/каталогов расположенных в корне файловой системы картотеки
     *
     * @param catalogId идентификатор картотеки
     * @return список файлов/каталогов
     */
    @Query("""
            SELECT f.id, f.folder_id, f.name, fh.mime_type, f.create_date, fh.size
            FROM       files        AS f
            INNER JOIN file_handles AS fh   ON fh.id   = f.file_handle_id
            INNER JOIN files        AS root ON root.id = f.folder_id
            WHERE f.catalog_id = :catalogId AND root.folder_id IS NULL AND root.uname = 'ROOT'
            LIMIT 16384
            """)
    List<FileInfo> findAll(long catalogId);


    /**
     * Получить список файлов/каталогов расположенных в указанном каталоге
     *
     * @param catalogId идентификатор картотеки
     * @param folderId  идентификатор каталога
     * @return список файлов/каталогов
     */
    @Query("""
            SELECT f.id, f.folder_id, f.name, fh.mime_type, f.create_date, fh.size
            FROM       files        AS f
            INNER JOIN file_handles AS fh  ON fh.id  = f.file_handle_id
            INNER JOIN files        AS fld ON fld.id = f.folder_id
            WHERE f.catalog_id = :catalogId AND fld.catalog_id = :catalogId AND fld.id = :folderId
            LIMIT 16384
            """)
    List<FileInfo> findAll(long catalogId, long folderId);

}
