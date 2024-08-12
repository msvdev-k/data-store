package ru.msvdev.ds.client.factory;

import ru.msvdev.ds.client.cartalog.Catalog;
import ru.msvdev.ds.client.datastore.DataStore;
import ru.msvdev.ds.client.field.Field;
import ru.msvdev.ds.client.model.cartalog.Catalog;
import ru.msvdev.ds.client.model.datastore.DataStore;
import ru.msvdev.ds.client.model.field.Field;
import ru.msvdev.ds.client.model.user.User;
import ru.msvdev.ds.client.openapi.ApiClient;
import ru.msvdev.ds.client.openapi.api.*;
import ru.msvdev.ds.client.provider.DataStoreProvider;


/**
 * Фабрика построителей сущностей, основанная на API взаимодействия
 * с сервером Data Store
 */
public class ApiBuilderFactory implements BuilderFactory {

    private final BuilderFactory builderFactory;

    private final User masterUser;

    private final CardApi cardApi;
    private final CatalogApi catalogApi;
    private final FieldApi fieldApi;
    private final FsApi fsApi;
    private final TagApi tagApi;
    private final UploadApi uploadApi;
    private final UserApi userApi;


    public ApiBuilderFactory(BuilderFactory builderFactory, DataStoreProvider dataStoreProvider) {
        this.builderFactory = builderFactory;
        this.masterUser = dataStoreProvider.getMasterUser();

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
                .masterUser(masterUser)
                .catalogApi(catalogApi);
    }

    @Override
    public Catalog.CatalogBuilder getCatalogBuilder() {
        return (Catalog.CatalogBuilder) builderFactory.getCatalogBuilder()
                .builderFactory(this)
                .catalogApi(catalogApi)
                .fieldApi(fieldApi)
                .userUuid(userUuid);
                .masterUser(masterUser);
    }


    @Override
    public Field.FieldBuilder getFieldBuilder() {
        return (Field.FieldBuilder) builderFactory.getFieldBuilder()
                .fieldApi(fieldApi)
                .masterUser(masterUser);
    }

}
