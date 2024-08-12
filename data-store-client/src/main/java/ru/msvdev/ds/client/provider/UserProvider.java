package ru.msvdev.ds.client.provider;

import ru.msvdev.ds.client.model.user.User;

/**
 * Базовый абстрактный провайдер, обеспечивающий доступ к сервису пользователей Data Store.
 * Все соответствующие провайдеры должны наследоваться от этого класса
 */
public abstract class UserProvider {

    /**
     * Аутентифицированный пользователь, прошедший проверку подлинности.
     * От имени этого пользователя должны осуществляются все запросы к Data Store.
     *
     * @return аутентифицированный пользователь
     */
    public abstract User getAuthUser();

}
