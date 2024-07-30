package ru.msvdev.ds.server.module.value.base.converter;

import ru.msvdev.ds.server.module.value.base.StringConverter;

import java.util.UUID;

public class UuidStringConverter implements StringConverter<UUID> {

    @Override
    public UUID fromString(String string) {
        return UUID.fromString(string);
    }

    @Override
    public String toString(UUID object) {
        return object.toString();
    }

}
