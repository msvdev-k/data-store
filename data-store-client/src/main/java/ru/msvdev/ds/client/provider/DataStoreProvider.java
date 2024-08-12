package ru.msvdev.ds.client.provider;

import ru.msvdev.ds.client.openapi.ApiClient;
import ru.msvdev.ds.client.openapi.Configuration;
import ru.msvdev.ds.client.model.user.User;

/**
 * Базовый абстрактный провайдер, обеспечивающий доступ к Data Store.
 * Все провайдеры должны наследоваться от этого класса
 */
public abstract class DataStoreProvider {

    /**
     * Получить класс конфигурации для классов клиентcкого API.
     * <p>
     * Класс является singleton и используется для создания экземпляров различных API классов.
     * API классы используют ApiClient только при создании и настройке соединения с Data Store,
     * и не хранят ссылку на него.
     * <p>
     * ApiClient является изменяемым и не синхронизированным, поэтому он не потокобезопасный.
     * API классы, созданные на его основе, являются неизменяемыми и потокобезопасными
     *
     * @return экземпляр ApiClient класса
     */
    public ApiClient getApiClient() {
        return Configuration.getDefaultApiClient();
    }


    /**
     * Пользователь, от имени которого осуществляются все запросы к Data Store.
     * В случае отсутствия пользователь считается неавторизованным
     *
     * @return пользователь осуществляющий запросы к Data Store
     */
    public abstract User getAuthUser();


    /**
     * Провайдер, обеспечивающий доступ к сервису пользователей
     *
     * @return UserProvider
     */
    public abstract UserProvider getUserProvider();
}
