package ru.msvdev.ds.server.security;

import java.util.List;
import java.util.UUID;

/**
 * Сервис, предоставляющий полномочия пользователя при работе с картотекой
 */
public interface UserAuthorityService {

    /**
     * Загрузить полномочия пользователя при работе с картотекой
     *
     * @param userUuid  идентификатор пользователя
     * @param catalogId идентификатор картотеки
     * @return список полномочий
     */
    List<Authority> loadAuthorities(UUID userUuid, long catalogId);

}
