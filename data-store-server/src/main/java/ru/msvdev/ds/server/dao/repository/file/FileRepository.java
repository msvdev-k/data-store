package ru.msvdev.ds.server.dao.repository.file;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.Repository;
import ru.msvdev.ds.server.dao.entity.file.ChunkingSchema;
import ru.msvdev.ds.server.dao.entity.file.FileInfo;

import java.time.OffsetDateTime;
import java.util.List;


/**
 * Репозиторий для управления файловой системой картотек
 */
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
            INSERT INTO files (catalog_id, container_id, name, mime_type, create_date)
            VALUES (:catalogId, (SELECT id FROM empty_container), 'ROOT', 'inode/directory', :createDate)
            """)
    boolean insertRoot(long catalogId, OffsetDateTime createDate);


    /**
     * Создать директорию в корне файловой системы картотеки
     *
     * @param catalogId  идентификатор картотеки
     * @param name       название создаваемой директории
     * @param createDate дата и время создания директории
     * @return идентификатор созданной директории
     */
    @Query("""
            WITH root_id AS (
                SELECT id FROM files
                WHERE catalog_id = :catalogId AND folder_id IS NULL AND uname = 'ROOT'
            ), inserted_folder AS (
                INSERT INTO files (catalog_id, folder_id, container_id, name, mime_type, create_date)
                VALUES (:catalogId, (SELECT id FROM root_id), (SELECT id FROM empty_container), :name, 'inode/directory', :createDate)
                RETURNING id
            )
            SELECT id FROM inserted_folder
            """)
    Long insertFolder(long catalogId, String name, OffsetDateTime createDate);


    /**
     * Создать директорию в файловой системе картотеки
     *
     * @param catalogId      идентификатор картотеки
     * @param parentFolderId идентификатор родительской директории
     * @param name           название создаваемой директории
     * @param createDate     дата и время создания директории
     * @return идентификатор созданной директории
     */
    @Query("""
            WITH folder_id AS (
                SELECT id FROM files
                WHERE id = :parentFolderId AND catalog_id = :catalogId
            ), inserted_folder AS (
                INSERT INTO files (catalog_id, folder_id, container_id, name, mime_type, create_date)
                VALUES (:catalogId, (SELECT id FROM folder_id), (SELECT id FROM empty_container), :name, 'inode/directory', :createDate)
                RETURNING id
            )
            SELECT id FROM inserted_folder
            """)
    Long insertFolder(long catalogId, long parentFolderId, String name, OffsetDateTime createDate);


    /**
     * Создать файл в корне файловой системы картотеки
     *
     * @param catalogId   идентификатор картотеки
     * @param name        название файла
     * @param containerId идентификатор контейнера с бинарными данными файла
     * @param mimeType    тип данных файла
     * @param createDate  дата и время создания файла
     * @return идентификатор созданного файла
     */
    @Query("""
            WITH root_id AS (
                SELECT id FROM files
                WHERE catalog_id = :catalogId AND folder_id IS NULL AND uname = 'ROOT'
            ), inserted_file AS (
                INSERT INTO files (catalog_id, folder_id, container_id, name, mime_type, create_date)
                VALUES (:catalogId, (SELECT id FROM root_id), :containerId, :name, :mimeType, :createDate)
                RETURNING id
            )
            SELECT id FROM inserted_file
            """)
    Long insertFile(long catalogId, String name, long containerId, String mimeType, OffsetDateTime createDate);


    /**
     * Создать файл в файловой системы картотеки
     *
     * @param catalogId      идентификатор картотеки
     * @param parentFolderId идентификатор родительского каталога
     * @param name           название создаваемого файла
     * @param containerId    идентификатор контейнера с бинарными данными файла
     * @param mimeType       тип данных файла
     * @param createDate     дата и время создания файла
     * @return идентификатор созданного файла
     */
    @Query("""
            WITH folder_id AS (
                SELECT id FROM files
                WHERE id = :parentFolderId AND catalog_id = :catalogId
            ), inserted_file AS (
                INSERT INTO files (catalog_id, folder_id, container_id, name, mime_type, create_date)
                VALUES (:catalogId, (SELECT id FROM folder_id), :containerId, :name, :mimeType, :createDate)
                RETURNING id
            )
            SELECT id FROM inserted_file
            """)
    Long insertFile(long catalogId, long parentFolderId, String name, long containerId, String mimeType, OffsetDateTime createDate);


    /**
     * Получить информацию о файле/каталоге по его ID
     *
     * @param catalogId идентификатор картотеки
     * @param fileId    идентификатор файла/каталога
     * @return объект с информацией о файле/каталоге
     */
    @Query("""
            SELECT f.id, COALESCE(f.folder_id, -1) AS folder_id, f.name, f.mime_type, f.create_date, c.size
            FROM files AS f
            INNER JOIN containers AS c ON c.id = f.container_id
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
            SELECT c.sha256
            FROM files AS f
            INNER JOIN containers AS c ON c.id = f.container_id
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
            SELECT c.size, c.chunk_count AS "count", c.chunk_size, c.last_chunk_size
            FROM files AS f
            INNER JOIN containers AS c ON c.id = f.container_id
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
            SELECT f.id, f.folder_id, f.name, f.mime_type, f.create_date, c.size
            FROM       files      AS f
            INNER JOIN containers AS c    ON c.id    = f.container_id
            INNER JOIN files      AS root ON root.id = f.folder_id
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
            SELECT f.id, f.folder_id, f.name, f.mime_type, f.create_date, c.size
            FROM       files      AS f
            INNER JOIN containers AS c   ON c.id   = f.container_id
            INNER JOIN files      AS fld ON fld.id = f.folder_id
            WHERE f.catalog_id = :catalogId AND fld.catalog_id = :catalogId AND fld.id = :folderId
            LIMIT 16384
            """)
    List<FileInfo> findAll(long catalogId, long folderId);

}
