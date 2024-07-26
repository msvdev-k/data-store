package ru.msvdev.ds.server.module.field.mapper;

import org.springframework.stereotype.Component;
import ru.msvdev.ds.server.module.field.entity.Field;
import ru.msvdev.ds.server.openapi.model.FieldResponse;
import ru.msvdev.ds.server.openapi.model.FieldTypes;

@Component
public class FieldRepositoryMapper {

    public FieldResponse convert(Field field) {
        FieldResponse fieldResponse = new FieldResponse();

        fieldResponse.setId(field.id());
        fieldResponse.setOrder(field.order());
        fieldResponse.setName(field.name());
        fieldResponse.setDescription(field.description());
        fieldResponse.setType(FieldTypes.valueOf(field.valueType().name()));
        fieldResponse.setFormat(field.format());

        return fieldResponse;
    }

}
