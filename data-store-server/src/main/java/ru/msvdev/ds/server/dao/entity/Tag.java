package ru.msvdev.ds.server.dao.entity;

import ru.msvdev.ds.server.utils.type.ValueType;

public record Tag(
        long cardId,
        long fieldId,
        ValueType valueType,
        long valueId
) {
}
