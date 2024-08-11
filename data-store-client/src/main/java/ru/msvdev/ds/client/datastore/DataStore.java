package ru.msvdev.ds.client.datastore;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NonNull;
import ru.msvdev.ds.client.cartalog.Catalog;
import ru.msvdev.ds.client.factory.BuilderFactory;
import ru.msvdev.ds.client.openapi.ApiException;
import ru.msvdev.ds.client.openapi.api.CatalogApi;
import ru.msvdev.ds.client.openapi.model.CatalogRequest;
import ru.msvdev.ds.client.openapi.model.CatalogResponse;

import java.util.List;
import java.util.UUID;


/**
 * Класс для работы с хранилищем данных
 */
@Builder
@AllArgsConstructor
public class DataStore {

    private final BuilderFactory builderFactory;

    private final CatalogApi catalogApi;
    private final UUID userUuid;


    /**
     * Получить список картотек
     *
     * @return список картотек
     */
    public List<Catalog> getCatalogs() throws ApiException {
        return catalogApi
                .catalogList(userUuid)
                .stream()
                .map(this::catalogResponseToCatalogMapper)
                .toList();
    }


    /**
     * Добавить картотеку
     *
     * @param name        название картотеки
     * @param description описание картотеки
     * @return добавленная картотека
     */
    public Catalog addCatalog(@NonNull String name, String description) throws ApiException {
        CatalogRequest request = new CatalogRequest()
                .name(name).description(description);

        CatalogResponse response = catalogApi.addCatalog(userUuid, request);
        return catalogResponseToCatalogMapper(response);
    }


    private Catalog catalogResponseToCatalogMapper(CatalogResponse response) {
        return builderFactory.getCatalogBuilder()
                .catalogResponse(response)
                .build();
    }
}
