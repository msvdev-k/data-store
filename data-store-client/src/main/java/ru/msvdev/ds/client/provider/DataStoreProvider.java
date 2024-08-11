package ru.msvdev.ds.client.provider;

import ru.msvdev.ds.client.openapi.ApiClient;
import ru.msvdev.ds.client.openapi.Configuration;

import java.util.UUID;

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
     * Уникальный идентификатор пользователя, осуществляющего запросы.
     * Все запросы к серверу ведутся от этого идентификатора. В случае отсутствия
     * идентификатора пользователь считается неавторизованным
     *
     * @return идентификатор пользователя
     */
    public abstract UUID getUserUuid();

}
