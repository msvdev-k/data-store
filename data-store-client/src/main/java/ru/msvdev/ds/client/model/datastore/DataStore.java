package ru.msvdev.ds.client.model.datastore;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import ru.msvdev.ds.client.model.cartalog.Catalog;
import ru.msvdev.ds.client.factory.BuilderFactory;
import ru.msvdev.ds.client.openapi.ApiException;
import ru.msvdev.ds.client.openapi.api.CatalogApi;
import ru.msvdev.ds.client.openapi.model.CatalogRequest;
import ru.msvdev.ds.client.openapi.model.CatalogResponse;
import ru.msvdev.ds.client.model.user.User;

import java.util.List;


/**
 * Класс для работы с хранилищем данных
 */
@Builder
@AllArgsConstructor
public class DataStore {

    private final BuilderFactory builderFactory;

    private final CatalogApi catalogApi;

    @Getter
    private final User authUser;


    /**
     * Получить список картотек
     *
     * @return список картотек
     */
    public List<Catalog> getCatalogs() throws ApiException {
        return catalogApi
                .catalogList(authUser.getUuid())
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

        CatalogResponse response = catalogApi.addCatalog(authUser.getUuid(), request);
        return catalogResponseToCatalogMapper(response);
    }


    private Catalog catalogResponseToCatalogMapper(CatalogResponse response) {
        return builderFactory.getCatalogBuilder()
                .catalogResponse(response)
                .build();
    }
}
