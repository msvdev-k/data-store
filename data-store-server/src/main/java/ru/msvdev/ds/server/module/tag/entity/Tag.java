package ru.msvdev.ds.server.module.tag.entity;

import ru.msvdev.ds.server.module.value.base.DataType;

public record Tag(
        long cardId,
        long fieldId,
        DataType dataType,
        long valueId
) {
}
