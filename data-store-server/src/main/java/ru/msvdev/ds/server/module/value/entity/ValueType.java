package ru.msvdev.ds.server.module.value.entity;

import ru.msvdev.ds.server.module.value.base.DataType;

public record ValueType(
        int id,
        DataType type,
        String description
) {
}
