package ru.msvdev.ds.client.field;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.msvdev.ds.client.base.DataStoreContainerTest;
import ru.msvdev.ds.client.cartalog.Catalog;
import ru.msvdev.ds.client.datastore.DataStore;
import ru.msvdev.ds.client.factory.ApiBuilderFactory;
import ru.msvdev.ds.client.factory.BuilderFactory;
import ru.msvdev.ds.client.openapi.ApiException;
import ru.msvdev.ds.client.openapi.model.FieldTypes;
import ru.msvdev.ds.client.provider.DataStoreProvider;
import ru.msvdev.ds.client.provider.ProviderConfiguration;
import ru.msvdev.ds.client.provider.TestDataStoreProvider;

import static org.junit.jupiter.api.Assertions.*;


public class FieldTest extends DataStoreContainerTest {

    private static ProviderConfiguration configuration;

    private Field field;


    @BeforeAll
    static void beforeAllInit() {
        configuration = DataStoreContainerTest.providerConfiguration();
    }

    @BeforeEach
    void init() throws ApiException {
        DataStoreProvider provider = TestDataStoreProvider.getInstance(configuration);

        ApiBuilderFactory builderFactory = new ApiBuilderFactory(
                new BuilderFactory() {
                },
                provider
        );

        DataStore dataStore = builderFactory.getDataStoreBuilder().build();
        Catalog catalog = dataStore.addCatalog("Test name", "Test description");
        field = catalog.addField(1, "Test Field", "Test Description", FieldTypes.INTEGER, "Test Format");
    }

    @Test
    void changeOrder() throws ApiException {
        // region Given
        Integer oldOrder = field.getOrder();
        String oldName = field.getName();
        String oldDescription = field.getDescription();

        Integer newOrder = 10;
        // endregion


        // region When
        field.changeOrder(newOrder);
        // endregion


        // region Then
        assertTrue(field.isValid());
        assertEquals(newOrder, field.getOrder());
        assertEquals(oldName, field.getName());
        assertEquals(oldDescription, field.getDescription());

        assertNotEquals(newOrder, oldOrder);
        // endregion
    }

    @Test
    void rename() throws ApiException {
        // region Given
        Integer oldOrder = field.getOrder();
        String oldName = field.getName();
        String oldDescription = field.getDescription();

        String newName = "New Test Name";
        // endregion


        // region When
        field.rename(newName);
        // endregion


        // region Then
        assertTrue(field.isValid());
        assertEquals(oldOrder, field.getOrder());
        assertEquals(newName, field.getName());
        assertEquals(oldDescription, field.getDescription());

        assertNotEquals(newName, oldName);
        // endregion
    }


    @Test
    void updateDescription() throws ApiException {
        // region Given
        Integer oldOrder = field.getOrder();
        String oldName = field.getName();
        String oldDescription = field.getDescription();

        String newDescription = "New Test Description";
        // endregion


        // region When
        field.updateDescription(newDescription);
        // endregion


        // region Then
        assertTrue(field.isValid());
        assertEquals(oldOrder, field.getOrder());
        assertEquals(oldName, field.getName());
        assertEquals(newDescription, field.getDescription());

        assertNotEquals(newDescription, oldDescription);
        // endregion
    }


    @Test
    void updateOrderAndNameAndDescription() throws ApiException {
        // region Given
        Integer oldOrder = field.getOrder();
        String oldName = field.getName();
        String oldDescription = field.getDescription();

        Integer newOrder = 10;
        String newName = "New Test Name";
        String newDescription = "New Test Description";
        // endregion


        // region When
        field.updateOrderAndNameAndDescription(newOrder, newName, newDescription);
        // endregion


        // region Then
        assertTrue(field.isValid());
        assertEquals(newOrder, field.getOrder());
        assertEquals(newName, field.getName());
        assertEquals(newDescription, field.getDescription());

        assertNotEquals(newOrder, oldOrder);
        assertNotEquals(newName, oldName);
        assertNotEquals(newDescription, oldDescription);
        // endregion
    }


    @Test
    void remove() throws ApiException {
        // region Given
        // endregion


        // region When
        field.remove();
        // endregion


        // region Then
        assertFalse(field.isValid());
        // endregion
    }

}
