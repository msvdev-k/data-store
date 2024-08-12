package ru.msvdev.ds.client.factory;

import ru.msvdev.ds.client.model.card.Card;
import ru.msvdev.ds.client.model.cartalog.Catalog;
import ru.msvdev.ds.client.model.datastore.DataStore;
import ru.msvdev.ds.client.model.field.Field;
import ru.msvdev.ds.client.model.tag.Tag;


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

    default Tag.TagBuilder getTagBuilder() {
        return Tag.builder();
    }
}
