package ru.msvdev.ds.server.module.field.entity;

import ru.msvdev.ds.server.module.value.base.DataType;

public record Field(
        long id,
        long catalogId,
        int order,
        String name,
        String description,
        DataType dataType,
        String format
) {
}
