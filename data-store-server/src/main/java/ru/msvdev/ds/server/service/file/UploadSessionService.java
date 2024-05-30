package ru.msvdev.ds.server.service.file;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.msvdev.ds.server.dao.entity.file.ChunkingSchema;
import ru.msvdev.ds.server.dao.entity.file.ContainerHeader;
import ru.msvdev.ds.server.dao.entity.file.UploadSession;
import ru.msvdev.ds.server.dao.repository.file.ChunkRepository;
import ru.msvdev.ds.server.dao.repository.file.ContainerRepository;
import ru.msvdev.ds.server.dao.repository.file.UploadSessionRepository;
import ru.msvdev.ds.server.mapper.UploadFileResponseMapper;
import ru.msvdev.ds.server.openapi.model.UploadChunkRequest;
import ru.msvdev.ds.server.openapi.model.UploadFileRequest;
import ru.msvdev.ds.server.openapi.model.UploadFileResponse;
import ru.msvdev.ds.server.openapi.model.UploadFileState;
import ru.msvdev.ds.server.property.UploadSessionProperty;
import ru.msvdev.ds.server.utils.file.UploadSessionState;

import java.time.OffsetDateTime;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class UploadSessionService {

    private final String EMPTY_CHUNK_CONTENT = "";

    private final UploadSessionProperty property;

    private final UploadSessionRepository uploadSessionRepository;
    private final ContainerRepository containerRepository;
    private final ChunkRepository chunkRepository;

    private final UploadFileResponseMapper uploadFileResponseMapper;


    /**
     * Открыть сессию выгрузки содержимого файла на сервер
     *
     * @param userUUID          идентификатор пользователя
     * @param uploadFileRequest запрос на открытие сессии
     * @param resetError        флаг сброса ошибки сессии
     * @return ответ сервера с параметрами выгрузки содержимого файла
     */
    @Transactional
    public UploadFileResponse openUploadSession(UUID userUUID, UploadFileRequest uploadFileRequest, Boolean resetError) {

        // Поиск существования контейнера с содержимым файла
        ContainerHeader containerHeader = containerRepository.findBySha256(uploadFileRequest.getSha256());
        if (containerHeader != null) {
            return uploadFileResponseMapper.getResponse(containerHeader);
        }

        // Поиск открытой сессии выгрузки содержимого файла
        UploadSession uploadSession = uploadSessionRepository.findBySha256(uploadFileRequest.getSha256());

        // Сессия не найдена. Открывается новая сессия
        if (uploadSession == null) {
            return addUploadSession(userUUID, uploadFileRequest);
        }

        // Сессия найдена и находится в состоянии обработки полученных фрагментов
        if (uploadSession.state() == UploadSessionState.PROCESSING) {
            return uploadFileResponseMapper.getResponse(uploadSession);
        }

        // Сессия найдена и находится в состоянии ошибки обработки полученных фрагментов
        if (uploadSession.state() == UploadSessionState.ERROR) {
            if (resetError) {
                boolean deleteSessionFlag = uploadSessionRepository.delete(uploadSession.id());
                if (deleteSessionFlag) {
                    return addUploadSession(userUUID, uploadFileRequest);
                }
            }
            return uploadFileResponseMapper.getResponse(uploadSession);
        }

        // Сессия найдена и находится в состоянии выгрузки фрагментов
        return addUploadChunkSession(userUUID, uploadSession);
    }


    /**
     * Добавить фрагмент файла
     *
     * @param userUUID           идентификатор пользователя
     * @param uploadChunkRequest зарос на выгрузку фрагмента файла на сервер
     * @return ответ сервера с параметрами выгрузки содержимого файла
     */
    @Transactional
    public UploadFileResponse addChunk(UUID userUUID, UploadChunkRequest uploadChunkRequest) {

        // Поиск открытой сессии выгрузки содержимого файла
        UploadSession uploadSession = uploadSessionRepository.findById(uploadChunkRequest.getUploadSession());
        if (uploadSession == null) {
            throw new RuntimeException("Сессия выгрузки файла не найдена (id=" + uploadChunkRequest.getUploadSession() + ")");
        }

        // Поиск идентификатора выгружаемого фрагмента файла
        Long chunkId = uploadSessionRepository.findChunkId(userUUID, uploadSession.id(), uploadChunkRequest.getChunkNumber());
        if (chunkId == null) {
            throw new RuntimeException("Сессия выгрузки фрагмента файла не найдена");
        }

        // Сохранить полученный фрагмент
        boolean updateContentFlag = chunkRepository.updateContent(chunkId, uploadChunkRequest.getContent());
        boolean updateUploadChunkState = uploadSessionRepository.updateUploadChunkState(
                userUUID, uploadSession.id(), uploadChunkRequest.getChunkNumber(),
                UploadSessionState.PROCESSING, OffsetDateTime.now()
        );
        if (!updateContentFlag || !updateUploadChunkState) {
            throw new RuntimeException("Сохранить фрагмент файла не удалось");
        }

        // Сформировать запрос на добавление следующего фрагмента файла
        return addUploadChunkSession(userUUID, uploadSession);
    }


    /**
     * Открыть новую сессию выгрузки содержимого файла на сервер
     *
     * @param userUUID          идентификатор пользователя
     * @param uploadFileRequest запрос на открытие сессии
     * @return ответ сервера с параметрами выгрузки содержимого файла
     */
    private UploadFileResponse addUploadSession(UUID userUUID, UploadFileRequest uploadFileRequest) {
        ChunkingSchema schema = ChunkingSchema.of(
                uploadFileRequest.getSize(),
                (int) property.chunkSize().toBytes(),
                (int) property.minChunkSize().toBytes());

        Long uploadSessionId = uploadSessionRepository.insert(UploadSessionState.UPLOAD, uploadFileRequest.getSha256(),
                schema.size(), schema.count(), schema.chunkSize(), schema.lastChunkSize());

        Long chunkId = chunkRepository.insert(schema.getChunkSize(ChunkingSchema.FIRST_CHUNK_NUMBER), EMPTY_CHUNK_CONTENT);

        boolean insertChunkFlag = uploadSessionRepository.insertUploadChunk(
                userUUID, uploadSessionId, chunkId, ChunkingSchema.FIRST_CHUNK_NUMBER,
                UploadSessionState.UPLOAD, OffsetDateTime.now());

        if (!insertChunkFlag)
            throw new RuntimeException("Ошибка открытия сессии добавления фрагмента файла");

        return uploadFileResponseMapper.getResponse(uploadSessionId, UploadFileState.UPLOAD, uploadFileRequest.getSha256(), schema, ChunkingSchema.FIRST_CHUNK_NUMBER);
    }


    /**
     * Открыть новую сессию выгрузки фрагмента файла на сервер
     *
     * @param userUUID      идентификатор пользователя
     * @param uploadSession сессия выгрузки содержимого файла
     * @return ответ сервера с параметрами выгрузки содержимого файла
     */
    private UploadFileResponse addUploadChunkSession(UUID userUUID, UploadSession uploadSession) {
        ChunkingSchema schema = new ChunkingSchema(uploadSession.size(), uploadSession.chunkCount(),
                uploadSession.chunkSize(), uploadSession.lastChunkSize());

        int[] chunkNumbers = uploadSessionRepository.findChunkNumbers(uploadSession.id());

        // Цикл поиска пустых фрагментов для выгрузки на сервер
        for (int i = 1; i <= schema.count(); i++) {
            if (i <= chunkNumbers.length && chunkNumbers[i - 1] == i) continue;

            Long chunkId = chunkRepository.insert(schema.getChunkSize(i), EMPTY_CHUNK_CONTENT);
            boolean insertChunkResult = uploadSessionRepository.insertUploadChunk(
                    userUUID, uploadSession.id(), chunkId, i,
                    UploadSessionState.UPLOAD, OffsetDateTime.now());

            if (!insertChunkResult)
                throw new RuntimeException("Ошибка открытия сессии добавления фрагмента файла");

            return uploadFileResponseMapper.getResponse(uploadSession.id(), UploadFileState.UPLOAD, uploadSession.sha256(), schema, i);
        }

        // Пустых фрагментов нет. Запускается состояние обработки полученных данных
        uploadSessionRepository.updateState(uploadSession.id(), UploadSessionState.PROCESSING);

        return uploadFileResponseMapper.getResponse(uploadSession.id(), UploadFileState.PROCESSING, uploadSession.sha256(), schema, 0);
    }

}
