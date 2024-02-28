package ru.msvdev.ds.server.security;

/**
 * Разрешение, выдаваемое при обращении к ресурсу
 */
public enum Permission {

    /**
     * 200 OK - доступ разрешён
     */
    OK,

    /**
     * 403 Forbidden - доступ запрещён
     */
    FORBIDDEN,

    /**
     * 400 Bad Request - некорректный запрос
     */
    BAD_REQUEST

}
