package ru.msvdev.ds.client.factory;

import ru.msvdev.ds.client.cartalog.Catalog;
import ru.msvdev.ds.client.datastore.DataStore;
import ru.msvdev.ds.client.field.Field;
import ru.msvdev.ds.client.openapi.ApiClient;
import ru.msvdev.ds.client.openapi.api.*;
import ru.msvdev.ds.client.provider.DataStoreProvider;

import java.util.UUID;


/**
 * Фабрика построителей сущностей, основанная на API взаимодействия
 * с сервером Data Store
 */
public class ApiBuilderFactory implements BuilderFactory {

    private final BuilderFactory builderFactory;

    private final UUID userUuid;

    private final CardApi cardApi;
    private final CatalogApi catalogApi;
    private final FieldApi fieldApi;
    private final FsApi fsApi;
    private final TagApi tagApi;
    private final UploadApi uploadApi;
    private final UserApi userApi;


    public ApiBuilderFactory(BuilderFactory builderFactory, DataStoreProvider dataStoreProvider) {
        this.builderFactory = builderFactory;
        this.userUuid = dataStoreProvider.getUserUuid();

        ApiClient apiClient = dataStoreProvider.getApiClient();
        cardApi = new CardApi(apiClient);
        catalogApi = new CatalogApi(apiClient);
        fieldApi = new FieldApi(apiClient);
        fsApi = new FsApi(apiClient);
        tagApi = new TagApi(apiClient);
        uploadApi = new UploadApi(apiClient);
        userApi = new UserApi(apiClient);
    }


    @Override
    public DataStore.DataStoreBuilder getDataStoreBuilder() {
        return builderFactory.getDataStoreBuilder()
                .builderFactory(this)
                .userUuid(userUuid)
                .catalogApi(catalogApi);
    }

    @Override
    public Catalog.CatalogBuilder getCatalogBuilder() {
        return (Catalog.CatalogBuilder) builderFactory.getCatalogBuilder()
                .builderFactory(this)
                .catalogApi(catalogApi)
                .fieldApi(fieldApi)
                .userUuid(userUuid);
    }


    @Override
    public Field.FieldBuilder getFieldBuilder() {
        return (Field.FieldBuilder) builderFactory.getFieldBuilder()
                .fieldApi(fieldApi)
                .userUuid(userUuid);
    }

}
