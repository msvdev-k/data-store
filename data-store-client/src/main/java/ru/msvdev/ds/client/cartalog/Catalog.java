package ru.msvdev.ds.client.cartalog;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import ru.msvdev.ds.client.base.Validated;
import ru.msvdev.ds.client.factory.BuilderFactory;
import ru.msvdev.ds.client.field.Field;
import ru.msvdev.ds.client.openapi.ApiException;
import ru.msvdev.ds.client.openapi.api.CatalogApi;
import ru.msvdev.ds.client.openapi.api.FieldApi;
import ru.msvdev.ds.client.openapi.model.*;

import java.util.List;
import java.util.UUID;


@EqualsAndHashCode
@AllArgsConstructor
@Builder(builderClassName = "DefaultCatalogBuilder")
public class Catalog implements Validated {

    private final BuilderFactory builderFactory;

    private final CatalogApi catalogApi;
    private final FieldApi fieldApi;
    private final UUID userUuid;

    private Long id;
    @Getter
    private String name;
    @Getter
    private String description;


    /**
     * Изменить название картотеки
     *
     * @param newName новое название картотеки
     */
    public void rename(String newName) throws ApiException {
        updateNameAndDescription(newName, null);
    }

    /**
     * Изменить описание картотеки
     *
     * @param newDescription новое описание картотеки
     */
    public void updateDescription(String newDescription) throws ApiException {
        updateNameAndDescription(null, newDescription);
    }

    /**
     * Изменить название и описание картотеки
     *
     * @param newName        новое название картотеки
     * @param newDescription новое описание картотеки
     */
    public void updateNameAndDescription(String newName, String newDescription) throws ApiException {
        CatalogRequest request = new CatalogRequest()
                .name(newName)
                .description(newDescription);

        CatalogResponse response = catalogApi.updateCatalogById(userUuid, id, request);

        if (!id.equals(response.getId())) {
            throw new RuntimeException("Update Catalog name or description error");
        }

        name = response.getName();
        description = response.getDescription();
    }


    /**
     * Полностью удалить картотеку из Data Store
     */
    public void remove() throws ApiException {
        catalogApi.removeCatalogById(userUuid, id);
        id = null;
        name = null;
        description = null;
    }


    /**
     * Получить список полей, которые можно прикреплять к карточкам
     *
     * @return список полей
     */
    public List<Field> getFields() throws ApiException {
        return fieldApi
                .fieldList(userUuid, id)
                .stream()
                .map(this::fieldResponseToFieldMapper)
                .toList();
    }


    /**
     * Добавить новое поле в картотеку
     *
     * @param order       Порядковый номер поля в карточке. Используется для сортировки полей при отображении карточек
     * @param name        Название поля
     * @param description Краткое описание поля
     * @param type        Тап данных ассоциированных с полем
     * @param format      Формат интерпретации данных клиентом (формат определяется логикой клиента)
     * @return добавленное поле
     */
    public Field addField(int order, String name, String description, FieldTypes type, String format) throws ApiException {
        FieldRequest request = new FieldRequest()
                .order(order)
                .name(name)
                .description(description)
                .type(type)
                .format(format);

        FieldResponse response = fieldApi.addField(userUuid, id, request);
        return fieldResponseToFieldMapper(response);
    }


    @Override
    public boolean isValid() {
        return id != null;
    }


    private Field fieldResponseToFieldMapper(FieldResponse response) {
        return builderFactory.getFieldBuilder()
                .fieldResponse(response)
                .build();
    }


    public static CatalogBuilder builder() {
        return new CatalogBuilder();
    }

    public static class CatalogBuilder extends DefaultCatalogBuilder {
        public CatalogBuilder catalogResponse(CatalogResponse catalogResponse) {
            this.id(catalogResponse.getId());
            this.name(catalogResponse.getName());
            this.description(catalogResponse.getDescription());
            return this;
        }
    }
}
