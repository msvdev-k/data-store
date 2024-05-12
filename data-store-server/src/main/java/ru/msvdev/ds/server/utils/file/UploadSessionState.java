package ru.msvdev.ds.server.utils.file;

/**
 * Состояния сессии выгрузки бинарных данных на сервер
 */
public enum UploadSessionState {
    /**
     * Состояние выгрузки данных
     */
    UPLOAD,

    /**
     * Состояние обработки выгруженных данных
     */
    PROCESSING,

    /**
     * Состояния ошибки обработки выгруженных данных
     */
    ERROR
}
