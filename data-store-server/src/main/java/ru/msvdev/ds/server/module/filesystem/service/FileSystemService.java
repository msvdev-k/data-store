package ru.msvdev.ds.server.module.filesystem.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.msvdev.ds.server.module.filesystem.entity.FileChunk;
import ru.msvdev.ds.server.module.filesystem.entity.FileInfo;
import ru.msvdev.ds.server.module.filesystem.mapper.DownloadResponseMapper;
import ru.msvdev.ds.server.module.filesystem.repository.DownloadRepository;
import ru.msvdev.ds.server.module.filesystem.repository.FileSystemRepository;
import ru.msvdev.ds.server.module.filesystem.mapper.FileSystemResponseMapper;
import ru.msvdev.ds.server.openapi.model.DownloadResponse;
import ru.msvdev.ds.server.openapi.model.FileSystemRequest;
import ru.msvdev.ds.server.openapi.model.FileSystemResponse;
import ru.msvdev.ds.server.openapi.model.RenameFileRequest;

import java.time.OffsetDateTime;
import java.util.List;


@Service
@RequiredArgsConstructor
public class FileSystemService {

    private final FileSystemRepository fileSystemRepository;
    private final DownloadRepository downloadRepository;

    private final FileSystemResponseMapper fileSystemResponseMapper;
    private final DownloadResponseMapper downloadResponseMapper;


    /**
     * Создать файловую систему для указанной картотеки.
     * Примечание: метод вызывается всего один раз при создании картотеки
     *
     * @param catalogId идентификатор картотеки
     * @return TRUE - файловая система создана, FALSE - не создана
     */
    @Transactional
    public boolean createFileSystem(long catalogId) {
        return fileSystemRepository.insertRoot(catalogId, OffsetDateTime.now());
    }


    /**
     * Получить список файлов и каталогов
     *
     * @param catalogId идентификатор картотеки
     * @param folderId  идентификатор каталога (0 - корневой каталог)
     * @return список заголовков файлов и каталогов
     */
    @Transactional(readOnly = true)
    public List<FileSystemResponse> findAll(long catalogId, long folderId) {
        List<FileInfo> files = folderId == 0 ?
                fileSystemRepository.findAll(catalogId) :
                fileSystemRepository.findAll(catalogId, folderId);

        return files.stream()
                .map(fileSystemResponseMapper::getResponse)
                .toList();
    }


    /**
     * Добавить файл или каталог в файловую систему картотеки
     *
     * @param catalogId         идентификатор картотеки
     * @param parentFolderId    идентификатор родительского каталога (0 - корневой каталог)
     * @param fileSystemRequest запрос на добавление файла или каталога
     * @return заголовок добавленного файла или каталога
     */
    @Transactional
    public FileSystemResponse addNode(long catalogId, long parentFolderId, FileSystemRequest fileSystemRequest) {
        String name = fileSystemRequest.getName();

        String mimeType = fileSystemRequest.getMimeType();
        String sha256 = fileSystemRequest.getSha256();

        FileInfo fileInfo = (mimeType == null || sha256 == null) ?
                insertFolder(catalogId, parentFolderId, name) :
                insertFile(catalogId, parentFolderId, name, mimeType, sha256);

        if (fileInfo == null) {
            throw new RuntimeException("Ошибка добавления файла или каталога");
        }

        return fileSystemResponseMapper.getResponse(fileInfo);
    }

    private FileInfo insertFolder(long catalogId, long parentFolderId, String name) {
        return parentFolderId == 0 ?
                fileSystemRepository.insertFolder(catalogId, name, OffsetDateTime.now()) :
                fileSystemRepository.insertFolder(catalogId, parentFolderId, name, OffsetDateTime.now());
    }

    private FileInfo insertFile(long catalogId, long parentFolderId, String name, String mimeType, String sha256) {
        if (mimeType.equals(FileInfo.DIRECTORY_MIME_TYPE)) {
            throw new RuntimeException("Некорректный тип файла");
        }

        Long containerId = fileSystemRepository.findContainerIdBySha256(sha256);

        if (containerId == null) {
            throw new RuntimeException("Контейнер с данными не найден!");
        }

        return parentFolderId == 0 ?
                fileSystemRepository.insertFile(catalogId, name, containerId, mimeType, OffsetDateTime.now()) :
                fileSystemRepository.insertFile(catalogId, parentFolderId, name, containerId, mimeType, OffsetDateTime.now());
    }


    /**
     * Переименовать файл или директорию
     *
     * @param catalogId         идентификатор картотеки
     * @param nodeId            идентификатор переименовываемого файла или каталога
     * @param renameFileRequest запрос на переименование
     * @return заголовок переименованного файла или каталога
     */
    public FileSystemResponse rename(long catalogId, long nodeId, RenameFileRequest renameFileRequest) {
        if (nodeId == 0) {
            throw new RuntimeException("Корневую директорию переименовать нельзя");
        }

        String newName = renameFileRequest.getNewName();
        if (newName.isBlank()) {
            throw new RuntimeException("Новое название некорректное");
        }

        boolean renameFlag = fileSystemRepository.rename(catalogId, nodeId, newName);
        if (!renameFlag) {
            throw new RuntimeException("Переименовать не удалось");
        }

        FileInfo fileInfo = fileSystemRepository.findById(catalogId, nodeId);
        if (fileInfo == null) {
            throw new RuntimeException("Ошибка переименования файла или каталога");
        }

        return fileSystemResponseMapper.getResponse(fileInfo);
    }


    /**
     * Удалить файл или директорию из файловой системы картотеки.
     * Примечание: директории удаляются каскадно со всем содержимым.
     * Примечание: удалять корневую директорию запрещено.
     * Внимание! Удалённые файлы восстановлению не подлежат
     *
     * @param catalogId идентификатор картотеки
     * @param nodeId    идентификатор удаляемого файла или каталога
     */
    public void remove(long catalogId, long nodeId) {
        if (nodeId == 0) {
            throw new RuntimeException("Корневую директорию удалить нельзя");
        }

        boolean removeFlag = fileSystemRepository.remove(catalogId, nodeId);
        if (!removeFlag) {
            throw new RuntimeException("Ошибка удаления файла или каталога");
        }
    }


    /**
     * Получить содержимое файла или его фрагмент
     *
     * @param catalogId   идентификатор картотеки
     * @param nodeId      идентификатор файла
     * @param chunkNumber порядковый номер фрагмента файла (нумерация отсчитывается от единицы)
     * @return ответ с запрашиваемыми данными
     */
    public DownloadResponse getFileContent(long catalogId, long nodeId, int chunkNumber) {
        FileChunk fileChunk = downloadRepository.findChunk(catalogId, nodeId, chunkNumber);
        if (fileChunk == null) {
            throw new RuntimeException("Не удалось найти содержимое файла");
        }

        return downloadResponseMapper.getResponse(nodeId, fileChunk);
    }
}
