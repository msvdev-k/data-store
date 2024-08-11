package ru.msvdev.ds.client.factory;

import ru.msvdev.ds.client.card.Card;
import ru.msvdev.ds.client.cartalog.Catalog;
import ru.msvdev.ds.client.datastore.DataStore;
import ru.msvdev.ds.client.field.Field;


/**
 * Фабрика построителей сущностей для работы с хранилищем данных
 */
public interface BuilderFactory {

    default DataStore.DataStoreBuilder getDataStoreBuilder() {
        return DataStore.builder();
    }

    default Catalog.CatalogBuilder getCatalogBuilder() {
        return Catalog.builder();
    }

    default Field.FieldBuilder getFieldBuilder() {
        return Field.builder();
    }

    default Card.CardBuilder getCardBuilder() {
        return Card.builder();
    }
}
