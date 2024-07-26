package ru.msvdev.ds.server.module.field.entity;

import ru.msvdev.ds.server.utils.type.ValueType;

public record Field(
        long id,
        long catalogId,
        int order,
        String name,
        String description,
        ValueType valueType,
        String format
) {
}
