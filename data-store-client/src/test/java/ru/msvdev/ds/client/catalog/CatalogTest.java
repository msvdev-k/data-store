package ru.msvdev.ds.client.catalog;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import ru.msvdev.ds.client.base.DataStoreContainerTest;
import ru.msvdev.ds.client.cartalog.Catalog;
import ru.msvdev.ds.client.datastore.DataStore;
import ru.msvdev.ds.client.factory.ApiBuilderFactory;
import ru.msvdev.ds.client.factory.BuilderFactory;
import ru.msvdev.ds.client.field.Field;
import ru.msvdev.ds.client.openapi.ApiException;
import ru.msvdev.ds.client.openapi.model.FieldTypes;
import ru.msvdev.ds.client.provider.DataStoreProvider;
import ru.msvdev.ds.client.provider.ProviderConfiguration;
import ru.msvdev.ds.client.provider.TestDataStoreProvider;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


public class CatalogTest extends DataStoreContainerTest {

    private static ProviderConfiguration configuration;

    private Catalog catalog;


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
        catalog = dataStore.addCatalog("Test name", "Test description");
    }


    @Test
    void rename() throws ApiException {
        // region Given
        String oldName = catalog.getName();
        String oldDescription = catalog.getDescription();
        String newName = "New Test Name";
        // endregion


        // region When
        catalog.rename(newName);
        // endregion


        // region Then
        assertTrue(catalog.isValid());
        assertEquals(newName, catalog.getName());
        assertEquals(oldDescription, catalog.getDescription());

        assertNotEquals(newName, oldName);
        // endregion
    }


    @Test
    void updateDescription() throws ApiException {
        // region Given
        String oldName = catalog.getName();
        String oldDescription = catalog.getDescription();
        String newDescription = "New Test Description";
        // endregion


        // region When
        catalog.updateDescription(newDescription);
        // endregion


        // region Then
        assertTrue(catalog.isValid());
        assertEquals(oldName, catalog.getName());
        assertEquals(newDescription, catalog.getDescription());

        assertNotEquals(newDescription, oldDescription);
        // endregion
    }


    @Test
    void updateNameAndDescription() throws ApiException {
        // region Given
        String oldName = catalog.getName();
        String oldDescription = catalog.getDescription();
        String newName = "New Test Name";
        String newDescription = "New Test Description";
        // endregion


        // region When
        catalog.updateNameAndDescription(newName, newDescription);
        // endregion


        // region Then
        assertTrue(catalog.isValid());
        assertEquals(newName, catalog.getName());
        assertEquals(newDescription, catalog.getDescription());

        assertNotEquals(newName, oldName);
        assertNotEquals(newDescription, oldDescription);
        // endregion
    }


    @Test
    void remove() throws ApiException {
        // region Given
        // endregion


        // region When
        catalog.remove();
        // endregion


        // region Then
        assertFalse(catalog.isValid());
        // endregion
    }


    @Test
    void getFields() throws ApiException {
        // region Given
        // endregion


        // region When
        List<Field> fields = catalog.getFields();
        // endregion


        // region Then
        assertTrue(fields.isEmpty());
        // endregion
    }


    @ParameterizedTest
    @EnumSource(FieldTypes.class)
    void addField(FieldTypes type) throws ApiException {
        // region Given
        int order = 1;
        String name = "Field Name";
        String description = "Some field description";
        String format = "length";
        // endregion


        // region When
        Field field = catalog.addField(order, name, description, type, format);
        // endregion


        // region Then
        assertTrue(field.isValid());
        assertEquals(order, field.getOrder());
        assertEquals(name, field.getName());
        assertEquals(description, field.getDescription());
        assertEquals(type, field.getType());
        assertEquals(format, field.getFormat());

        List<Field> fields = catalog.getFields();
        assertEquals(1, fields.size());
        assertTrue(fields.contains(field));
        // endregion
    }
}
