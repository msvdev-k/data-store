package ru.msvdev.ds.client.datastore;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.msvdev.ds.client.base.DataStoreContainerTest;
import ru.msvdev.ds.client.cartalog.Catalog;
import ru.msvdev.ds.client.factory.ApiBuilderFactory;
import ru.msvdev.ds.client.factory.BuilderFactory;
import ru.msvdev.ds.client.openapi.ApiException;
import ru.msvdev.ds.client.provider.DataStoreProvider;
import ru.msvdev.ds.client.provider.TestDataStoreProvider;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class DataStoreTest {

    private static String dataStoreHost;
    private static int dataStorePort;

    private DataStore dataStore;

    @BeforeAll
    static void beforeAllInit() {
        dataStoreHost = DataStoreContainerTest.dataStoreHost();
        dataStorePort = DataStoreContainerTest.dataStorePort();
    }


    @BeforeEach
    void init() {
        DataStoreProvider provider = TestDataStoreProvider.getInstance(dataStoreHost, dataStorePort);

        ApiBuilderFactory builderFactory = new ApiBuilderFactory(
                new BuilderFactory() {
                },
                provider
        );

        dataStore = builderFactory.getDataStoreBuilder().build();
    }


    @Test
    void getCatalogs() throws ApiException {
        // region Given
        // endregion


        // region When
        List<Catalog> catalogs = dataStore.getCatalogs();
        // endregion


        // region Then
        assertTrue(catalogs.isEmpty());
        // endregion
    }


    @Test
    void addCatalog() throws ApiException {
        // region Given
        String name = "Название картотеки";
        String description = "Краткое описание...";
        // endregion


        // region When
        Catalog catalog = dataStore.addCatalog(name, description);
        // endregion


        // region Then
        assertEquals(name, catalog.getName());
        assertEquals(description, catalog.getDescription());

        List<Catalog> catalogs = dataStore.getCatalogs();
        assertEquals(1, catalogs.size());
        assertTrue(catalogs.contains(catalog));
        // endregion
    }

}
