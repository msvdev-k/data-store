package ru.msvdev.ds.server.data.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ru.msvdev.ds.server.data.entity.Field;
import ru.msvdev.ds.server.data.repository.FieldRepository;
import ru.msvdev.ds.server.utils.type.ValueType;
import ru.msvdev.ds.server.openapi.model.FieldRequest;
import ru.msvdev.ds.server.openapi.model.FieldResponse;
import ru.msvdev.ds.server.openapi.model.FieldTypes;

import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class FieldService {

    private static final int DEFAULT_ORDER = 1;

    private final FieldRepository fieldRepository;


    @Transactional
    public FieldResponse newField(Long catalogId, FieldRequest fieldRequest) {
        int order = fieldRequest.getOrder() != null ? fieldRequest.getOrder() : DEFAULT_ORDER;
        String name = fieldRequest.getName();
        ValueType type = ValueType.valueOf(fieldRequest.getType().name());
        String description = fieldRequest.getDescription();
        String format = fieldRequest.getFormat();

        if (name == null || name.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Название поля не должно быть пустым!");
        }

        if (description != null && description.isBlank()) {
            description = null;
        }

        if (format != null && format.isBlank()) {
            format = null;
        }

        Field field = fieldRepository.insert(catalogId, order, name, type, format, description);

        if (field == null) {
            throw new RuntimeException("Создать поле не удалось");
        }

        return convert(field);
    }


    @Transactional(readOnly = true)
    public List<FieldResponse> getAllFields(Long catalogId) {
        return fieldRepository
                .findAll(catalogId)
                .stream()
                .map(this::convert)
                .toList();
    }


    @Transactional
    public void deleteField(Long catalogId, Long fieldId) {
        boolean result = fieldRepository.deleteById(catalogId, fieldId);
        if (!result) {
            throw new RuntimeException("Удалить поле не удалось");
        }
    }


    @Transactional
    public FieldResponse updateField(Long catalogId, Long fieldId, FieldRequest fieldRequest) {
        Integer order = fieldRequest.getOrder();
        String name = fieldRequest.getName();
        String description = fieldRequest.getDescription();

        if (order != null) {
            boolean result = fieldRepository.updateOrder(catalogId, fieldId, order);
            if (!result) {
                throw new RuntimeException("Ошибка изменения порядкового номера поля");
            }
        }

        if (name != null && !name.isBlank()) {
            boolean result = fieldRepository.updateName(catalogId, fieldId, name.trim());
            if (!result) {
                throw new RuntimeException("Ошибка переименования поля");
            }
        }

        if (description != null) {
            if (description.isBlank()) {
                description = null;
            } else {
                description = description.trim();
            }
            boolean result = fieldRepository.updateDescription(catalogId, fieldId, description);
            if (!result) {
                throw new RuntimeException("Ошибка изменения описания поля");
            }
        }

        Optional<Field> optionalField = fieldRepository.findById(catalogId, fieldId);

        if (optionalField.isEmpty()) {
            throw new RuntimeException("Поле не найдено");
        }

        return convert(optionalField.get());
    }


    @Transactional(readOnly = true)
    public ValueType getValueType(Long catalogId, Long fieldId) {
        return fieldRepository
                .findById(catalogId, fieldId)
                .map(Field::valueType)
                .orElseThrow();
    }


    private FieldResponse convert(Field field) {
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
