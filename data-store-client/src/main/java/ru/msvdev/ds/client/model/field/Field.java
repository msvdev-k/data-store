package ru.msvdev.ds.client.model.field;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import ru.msvdev.ds.client.base.Validated;
import ru.msvdev.ds.client.model.cartalog.Catalog;
import ru.msvdev.ds.client.openapi.ApiException;
import ru.msvdev.ds.client.openapi.api.FieldApi;
import ru.msvdev.ds.client.openapi.model.FieldRequest;
import ru.msvdev.ds.client.openapi.model.FieldResponse;
import ru.msvdev.ds.client.openapi.model.FieldTypes;
import ru.msvdev.ds.client.model.user.User;


@EqualsAndHashCode
@AllArgsConstructor
@Builder(builderClassName = "DefaultFieldBuilder")
public class Field implements Validated {

    private final FieldApi fieldApi;

    @Getter
    private final Catalog catalog;
    @Getter
    private final User masterUser;

    @Getter
    private Long id;
    @Getter
    private Integer order;
    @Getter
    private String name;
    @Getter
    private String description;
    @Getter
    private FieldTypes type;
    @Getter
    private String format;


    /**
     * Изменить порядковый номер поля при отображении карточки
     *
     * @param newOrder новое значение порядкового номера
     */
    public void changeOrder(int newOrder) throws ApiException {
        updateOrderAndNameAndDescription(newOrder, null, null);
    }


    /**
     * Переименовать поле
     *
     * @param newName новое название поля
     */
    public void rename(String newName) throws ApiException {
        updateOrderAndNameAndDescription(-1, newName, null);
    }


    /**
     * Обновить описание поля
     *
     * @param newDescription новое описание поля
     */
    public void updateDescription(String newDescription) throws ApiException {
        updateOrderAndNameAndDescription(-1, null, newDescription);
    }

    /**
     * Изменить порядковый номер, название и описание поля
     *
     * @param newOrder       новое значение порядкового номера
     * @param newName        новое название поля
     * @param newDescription новое описание поля
     */
    public void updateOrderAndNameAndDescription(int newOrder, String newName, String newDescription) throws ApiException {
        FieldRequest request = new FieldRequest()
                .order(newOrder > 0 ? newOrder : null)
                .name(newName)
                .description(newDescription);

        FieldResponse response = fieldApi.updateFieldById(masterUser.getUuid(), catalog.getId(), id, request);

        if (!id.equals(response.getId())) {
            throw new RuntimeException("Update field order or name or description error");
        }

        order = response.getOrder();
        name = response.getName();
        description = response.getDescription();
    }


    /**
     * Удалить поле из картотеки
     */
    public void remove() throws ApiException {
        fieldApi.removeFieldById(masterUser.getUuid(), catalog.getId(), id);
        id = null;
        order = null;
        name = null;
        description = null;
        type = null;
        format = null;
    }


    @Override
    public boolean isValid() {
        return id != null && catalog.isValid();
    }


    public static FieldBuilder builder() {
        return new FieldBuilder();
    }

    public static class FieldBuilder extends DefaultFieldBuilder {
        public FieldBuilder fieldResponse(FieldResponse fieldResponse) {
            this.id(fieldResponse.getId());
            this.order(fieldResponse.getOrder());
            this.name(fieldResponse.getName());
            this.description(fieldResponse.getDescription());
            this.type(fieldResponse.getType());
            this.format(fieldResponse.getFormat());
            return this;
        }
    }
}
