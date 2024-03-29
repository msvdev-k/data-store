package ru.msvdev.ds.server.data.entity;

import ru.msvdev.ds.server.utils.type.ValueType;

public record Tag(
        Long cardId,
        Long fieldId,
        ValueType valueType,
        Long valueId
) {
}
