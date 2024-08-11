package ru.msvdev.ds.client.provider;

import lombok.Getter;
import ru.msvdev.ds.client.openapi.ApiClient;

import java.util.UUID;

/**
 * Базовый абстрактный провайдер, обеспечивающий доступ к Data Store.
 * Все провайдеры должны наследоваться от этого класса
 */
@Getter
public abstract class DataStoreProvider {

    protected final ApiClient apiClient;

    public DataStoreProvider(ApiClient apiClient) {
        this.apiClient = apiClient;
    }


    /**
     * Уникальный идентификатор пользователя, осуществляющего запросы
     *
     * @return идентификатор пользователя
     */
    public abstract UUID getUserUuid();

}
