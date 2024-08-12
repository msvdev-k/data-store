package ru.msvdev.ds.client.model.cartalog;

import lombok.*;
import ru.msvdev.ds.client.base.Validated;
import ru.msvdev.ds.client.model.card.Card;
import ru.msvdev.ds.client.factory.BuilderFactory;
import ru.msvdev.ds.client.model.field.Field;
import ru.msvdev.ds.client.openapi.ApiException;
import ru.msvdev.ds.client.openapi.api.CardApi;
import ru.msvdev.ds.client.openapi.api.CatalogApi;
import ru.msvdev.ds.client.openapi.api.FieldApi;
import ru.msvdev.ds.client.openapi.model.*;
import ru.msvdev.ds.client.model.user.User;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@EqualsAndHashCode
@AllArgsConstructor
@Builder(builderClassName = "DefaultCatalogBuilder")
public class Catalog implements Validated {

    private final BuilderFactory builderFactory;

    private final CatalogApi catalogApi;
    private final FieldApi fieldApi;
    private final CardApi cardApi;

    @Getter
    private final User authUser;

    @Getter
    private Long id;
    @Getter
    private String name;
    @Getter
    private String description;


    private Map<Long, Field> fieldMap;


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

        CatalogResponse response = catalogApi.updateCatalogById(authUser.getUuid(), id, request);

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
        catalogApi.removeCatalogById(authUser.getUuid(), id);
        id = null;
        name = null;
        description = null;
        fieldMap = null;
    }


    /**
     * Получить поле, прикрепляемое к карточке по его идентификатору
     *
     * @param fieldId уникальный идентификатор поля
     * @return поле, прикрепляемое к карточке, либо null если такого идентификатора не существует
     */
    public Field getField(Long fieldId) throws ApiException {
        if (fieldMap == null || fieldMap.isEmpty()) {
            getFields(false);
        }

        return fieldMap.get(fieldId);
    }

    /**
     * Получить список полей, которые можно прикреплять к карточкам
     *
     * @return список полей
     */
    public List<Field> getFields() throws ApiException {
        return getFields(true);
    }

    /**
     * Получить список полей, которые можно прикреплять к карточкам
     *
     * @param fromCash true - список получается из кеша, false - из Data Store
     * @return список полей
     */
    public List<Field> getFields(boolean fromCash) throws ApiException {
        if (fromCash && fieldMap != null && !fieldMap.isEmpty()) {
            return fieldMap.values().stream().toList();
        }

        List<Field> fields = fieldApi
                .fieldList(authUser.getUuid(), id)
                .stream()
                .map(this::fieldResponseToFieldMapper)
                .toList();

        fieldMap = fields.stream()
                .collect(
                        Collectors.toMap(Field::getId, field -> field)
                );

        return fields;
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

        FieldResponse response = fieldApi.addField(authUser.getUuid(), id, request);
        return fieldResponseToFieldMapper(response);
    }


    /**
     * Получить список карточек.
     * На данный момент из каталога выбираются все карточки.
     * В будущем будет добавлена возможность получения списка на основе фильтра.
     *
     * @return список карточек
     */
    public List<Card> getCards() throws ApiException {
        return cardApi
                .cardList(authUser.getUuid(), id, 1, 0)
                .stream()
                .map(this::cardResponseToCardMapper)
                .toList();
    }


    @Override
    public boolean isValid() {
        return id != null;
    }


    @SneakyThrows
    private Card cardResponseToCardMapper(CardResponse response) {
        return builderFactory
                .getCardBuilder()
                .cardResponse(response, this::getField, builderFactory::getTagBuilder)
                .catalog(this)
                .build();
    }


    private Field fieldResponseToFieldMapper(FieldResponse response) {
        return builderFactory.getFieldBuilder()
                .fieldResponse(response)
                .catalog(this)
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
