package ru.msvdev.ds.client.factory;

import ru.msvdev.ds.client.model.card.Card;
import ru.msvdev.ds.client.model.cartalog.Catalog;
import ru.msvdev.ds.client.model.datastore.DataStore;
import ru.msvdev.ds.client.model.field.Field;
import ru.msvdev.ds.client.model.tag.Tag;
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

    private final User authUser;

    private final CardApi cardApi;
    private final CatalogApi catalogApi;
    private final FieldApi fieldApi;
    private final FsApi fsApi;
    private final TagApi tagApi;
    private final UploadApi uploadApi;
    private final UserApi userApi;


    public ApiBuilderFactory(BuilderFactory builderFactory, DataStoreProvider dataStoreProvider) {
        this.builderFactory = builderFactory;
        this.authUser = dataStoreProvider.getAuthUser();

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
                .authUser(authUser)
                .catalogApi(catalogApi);
    }

    @Override
    public Catalog.CatalogBuilder getCatalogBuilder() {
        return (Catalog.CatalogBuilder) builderFactory.getCatalogBuilder()
                .builderFactory(this)
                .catalogApi(catalogApi)
                .fieldApi(fieldApi)
                .cardApi(cardApi)
                .authUser(authUser);
    }


    @Override
    public Field.FieldBuilder getFieldBuilder() {
        return (Field.FieldBuilder) builderFactory.getFieldBuilder()
                .fieldApi(fieldApi)
                .authUser(authUser);
    }


    @Override
    public Card.CardBuilder getCardBuilder() {
        return (Card.CardBuilder) builderFactory.getCardBuilder()
                .builderFactory(this)
                .cardApi(cardApi)
                .tagApi(tagApi)
                .authUser(authUser);
    }


    @Override
    public Tag.TagBuilder getTagBuilder() {
        return builderFactory.getTagBuilder();
    }
}
