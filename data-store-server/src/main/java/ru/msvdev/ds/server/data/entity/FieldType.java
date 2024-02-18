package ru.msvdev.ds.server.data.entity;

import ru.msvdev.ds.server.utils.type.ValueType;

public record FieldType(
        int id,
        ValueType type,
        String description
) {
}
