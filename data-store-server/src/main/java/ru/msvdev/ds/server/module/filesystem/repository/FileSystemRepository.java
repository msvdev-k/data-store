package ru.msvdev.ds.server.module.filesystem.repository;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.Repository;
import ru.msvdev.ds.server.module.filesystem.entity.FileInfo;

import java.time.OffsetDateTime;
import java.util.List;


/**
 * Репозиторий для управления файловой системой картотек
 */
public interface FileSystemRepository extends Repository<FileInfo, Long> {

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
            VALUES (:catalogId, (SELECT id FROM empty_container), '$ROOT$', 'inode/directory', :createDate)
            """)
    boolean insertRoot(long catalogId, OffsetDateTime createDate);


    /**
     * Создать директорию в корне файловой системы картотеки
     *
     * @param catalogId  идентификатор картотеки
     * @param name       название создаваемой директории
     * @param createDate дата и время создания директории
     * @return дескриптор созданной директории
     */
    @Query("""
            WITH root_id AS (
                SELECT id FROM files
                WHERE catalog_id = :catalogId AND folder_id IS NULL AND uname = '$ROOT$'
            ), inserted_folder AS (
                INSERT INTO files (catalog_id, folder_id, container_id, name, mime_type, create_date)
                VALUES (:catalogId, (SELECT id FROM root_id), (SELECT id FROM empty_container), :name, 'inode/directory', :createDate)
                RETURNING *
            )
            SELECT f.id, f.name, f.mime_type, f.create_date, c.size
            FROM inserted_folder  AS f
            INNER JOIN containers AS c ON c.id = f.container_id
            """)
    FileInfo insertFolder(long catalogId, String name, OffsetDateTime createDate);


    /**
     * Создать директорию в файловой системе картотеки
     *
     * @param catalogId      идентификатор картотеки
     * @param parentFolderId идентификатор родительской директории
     * @param name           название создаваемой директории
     * @param createDate     дата и время создания директории
     * @return дескриптор созданной директории
     */
    @Query("""
            WITH parent_folder_id AS (
                SELECT id FROM files
                WHERE id = :parentFolderId AND catalog_id = :catalogId AND mime_type = 'inode/directory'
            ), inserted_folder AS (
                INSERT INTO files (catalog_id, folder_id, container_id, name, mime_type, create_date)
                VALUES (:catalogId, (SELECT id FROM parent_folder_id), (SELECT id FROM empty_container), :name, 'inode/directory', :createDate)
                RETURNING *
            )
            SELECT f.id, f.name, f.mime_type, f.create_date, c.size
            FROM inserted_folder  AS f
            INNER JOIN containers AS c ON c.id = f.container_id
            """)
    FileInfo insertFolder(long catalogId, long parentFolderId, String name, OffsetDateTime createDate);


    /**
     * Создать файл в корне файловой системы картотеки
     *
     * @param catalogId   идентификатор картотеки
     * @param name        название файла
     * @param containerId идентификатор контейнера с бинарными данными файла
     * @param mimeType    тип данных файла
     * @param createDate  дата и время создания файла
     * @return дескриптор созданного файла
     */
    @Query("""
            WITH root_id AS (
                SELECT id FROM files
                WHERE catalog_id = :catalogId AND folder_id IS NULL AND uname = '$ROOT$'
            ), inserted_file AS (
                INSERT INTO files (catalog_id, folder_id, container_id, name, mime_type, create_date)
                VALUES (:catalogId, (SELECT id FROM root_id), :containerId, :name, :mimeType, :createDate)
                RETURNING *
            )
            SELECT f.id, f.name, f.mime_type, f.create_date, c.size
            FROM inserted_file    AS f
            INNER JOIN containers AS c ON c.id = f.container_id
            """)
    FileInfo insertFile(long catalogId, String name, long containerId, String mimeType, OffsetDateTime createDate);


    /**
     * Создать файл в файловой системы картотеки
     *
     * @param catalogId      идентификатор картотеки
     * @param parentFolderId идентификатор родительского каталога
     * @param name           название создаваемого файла
     * @param containerId    идентификатор контейнера с бинарными данными файла
     * @param mimeType       тип данных файла
     * @param createDate     дата и время создания файла
     * @return дескриптор созданного файла
     */
    @Query("""
            WITH parent_folder_id AS (
                SELECT id FROM files
                WHERE id = :parentFolderId AND catalog_id = :catalogId AND mime_type = 'inode/directory'
            ), inserted_file AS (
                INSERT INTO files (catalog_id, folder_id, container_id, name, mime_type, create_date)
                VALUES (:catalogId, (SELECT id FROM parent_folder_id), :containerId, :name, :mimeType, :createDate)
                RETURNING *
            )
            SELECT f.id, f.name, f.mime_type, f.create_date, c.size
            FROM inserted_file    AS f
            INNER JOIN containers AS c ON c.id = f.container_id
            """)
    FileInfo insertFile(long catalogId, long parentFolderId, String name, long containerId, String mimeType, OffsetDateTime createDate);


    /**
     * Получить информацию о файле или каталоге по его ID
     *
     * @param catalogId идентификатор картотеки
     * @param nodeId    идентификатор файла или каталога
     * @return дескриптор файла
     */
    @Query("""
            SELECT f.id, f.name, f.mime_type, f.create_date, c.size
            FROM files AS f
            INNER JOIN containers AS c ON c.id = f.container_id
            WHERE f.id = :nodeId AND f.catalog_id = :catalogId
            """)
    FileInfo findById(long catalogId, long nodeId);


    /**
     * Переименовать файл или каталог
     *
     * @param catalogId идентификатор картотеки
     * @param nodeId    идентификатор файла или каталога
     * @param newName   новое название файли или каталога
     * @return флаг операции: TRUE - успех, FALSE - операция не выполнена
     */
    @Modifying
    @Query("UPDATE files SET name = :newName WHERE id = :nodeId AND catalog_id = :catalogId")
    boolean rename(long catalogId, long nodeId, String newName);


    /**
     * Удалить файл или директорию из файловой системы картотеки.
     * Директории удаляются каскадно со всем содержимым.
     * Корневую директорию удалить нельзя
     *
     * @param catalogId идентификатор картотеки
     * @param nodeId    идентификатор удаляемого файла или каталога
     */
    @Modifying
    @Query("DELETE FROM files WHERE id = :nodeId AND catalog_id = :catalogId AND uname != '$ROOT$'")
    boolean remove(long catalogId, long nodeId);


    /**
     * Получить список файлов и каталогов расположенных в корне файловой системы картотеки
     *
     * @param catalogId идентификатор картотеки
     * @return список файлов и каталогов
     */
    @Query("""
            SELECT f.id, f.name, f.mime_type, f.create_date, c.size
            FROM       files      AS f
            INNER JOIN containers AS c    ON c.id    = f.container_id
            INNER JOIN files      AS root ON root.id = f.folder_id
            WHERE f.catalog_id = :catalogId AND root.folder_id IS NULL AND root.uname = '$ROOT$'
            LIMIT 16384
            """)
    List<FileInfo> findAll(long catalogId);


    /**
     * Получить список файлов и каталогов расположенных в указанной директории
     *
     * @param catalogId идентификатор картотеки
     * @param folderId  идентификатор директории
     * @return список файлов и каталогов
     */
    @Query("""
            SELECT f.id, f.name, f.mime_type, f.create_date, c.size
            FROM       files      AS f
            INNER JOIN containers AS c   ON c.id   = f.container_id
            INNER JOIN files      AS fld ON fld.id = f.folder_id
            WHERE f.catalog_id = :catalogId AND fld.catalog_id = :catalogId AND fld.id = :folderId
            LIMIT 16384
            """)
    List<FileInfo> findAll(long catalogId, long folderId);


    /**
     * Найти идентификатор контейнера с бинарными данными по его hash-сумме.
     * В данном контейнере хранится содержимое файла
     *
     * @param sha256 hash-сумма (SHA-256)
     * @return идентификатор контейнера с данными
     */
    @Query("SELECT id FROM containers WHERE sha256 = :sha256")
    Long findContainerIdBySha256(String sha256);

}
