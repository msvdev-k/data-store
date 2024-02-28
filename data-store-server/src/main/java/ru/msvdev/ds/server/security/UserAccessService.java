package ru.msvdev.ds.server.security;

/**
 * Сервис, проверяющий допуск пользователя к запрашиваемому ресурсу
 */
public interface UserAccessService {

    /**
     * Получить разрешение на доступ к результату запроса
     *
     * @param httpRequest запрос
     * @return разрешение
     */
    Permission getPermission(HttpRequest httpRequest);

}
