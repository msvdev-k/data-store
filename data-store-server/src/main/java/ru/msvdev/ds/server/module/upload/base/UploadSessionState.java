package ru.msvdev.ds.server.module.upload.base;

/**
 * Состояния сессии выгрузки содержимого файла на сервер
 */
public enum UploadSessionState {
    /**
     * Состояние активной выгрузки данных
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
