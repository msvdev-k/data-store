package ru.msvdev.ds.server.utils.file;

public enum UploadSessionState {
    /**
     * Состояние выгрузки файла
     */
    UPLOAD,

    /**
     * Состояние обработки выгруженного файла
     */
    PROCESSING,

    /**
     * Состояния ошибки обработки файла
     */
    ERROR
}
