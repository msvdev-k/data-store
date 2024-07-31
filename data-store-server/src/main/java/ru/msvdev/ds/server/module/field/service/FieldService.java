package ru.msvdev.ds.server.module.field.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ru.msvdev.ds.server.module.field.entity.Field;
import ru.msvdev.ds.server.module.field.mapper.FieldRepositoryMapper;
import ru.msvdev.ds.server.module.field.repository.FieldRepository;
import ru.msvdev.ds.server.module.value.base.DataType;
import ru.msvdev.ds.server.openapi.model.FieldRequest;
import ru.msvdev.ds.server.openapi.model.FieldResponse;

import java.util.List;


@Service
@RequiredArgsConstructor
public class FieldService {

    private static final int DEFAULT_ORDER = 1;

    private final FieldRepository fieldRepository;
    private final FieldRepositoryMapper fieldRepositoryMapper;


    @Transactional
    public FieldResponse newField(long catalogId, FieldRequest fieldRequest) {

        String name = fieldRequest.getName();
        if (name == null || name.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Название поля не должно быть пустым!");
        }

        String description = fieldRequest.getDescription();
        if (description != null && description.isBlank()) {
            description = null;
        }

        String format = fieldRequest.getFormat();
        if (format != null && format.isBlank()) {
            format = null;
        }

        int order = fieldRequest.getOrder() != null ? fieldRequest.getOrder() : DEFAULT_ORDER;
        DataType type = DataType.valueOf(fieldRequest.getType().name());

        Field field = fieldRepository.insert(catalogId, order, name, type, format, description);

        if (field == null) {
            throw new RuntimeException("Создать поле не удалось");
        }

        return fieldRepositoryMapper.convert(field);
    }


    @Transactional(readOnly = true)
    public List<FieldResponse> getAllFields(long catalogId) {
        return fieldRepository
                .findAll(catalogId)
                .stream()
                .map(fieldRepositoryMapper::convert)
                .toList();
    }


    @Transactional
    public void deleteField(long catalogId, long fieldId) {
        boolean result = fieldRepository.deleteById(catalogId, fieldId);
        if (!result) {
            throw new RuntimeException("Удалить поле не удалось");
        }
    }


    @Transactional
    public FieldResponse updateField(long catalogId, long fieldId, FieldRequest fieldRequest) {
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

        Field field = fieldRepository.findById(catalogId, fieldId);

        if (field == null) {
            throw new RuntimeException("Поле не найдено");
        }

        return fieldRepositoryMapper.convert(field);
    }


    @Transactional(readOnly = true)
    public DataType getValueType(long catalogId, long fieldId) {
        Field field = fieldRepository.findById(catalogId, fieldId);
        if (field == null)
            throw new RuntimeException();
        return field.dataType();
    }


    public boolean existsById(long catalogId, long fieldId) {
        return fieldRepository.existsById(catalogId, fieldId);
    }

}
