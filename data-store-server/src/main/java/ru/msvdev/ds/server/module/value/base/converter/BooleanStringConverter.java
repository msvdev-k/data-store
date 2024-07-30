package ru.msvdev.ds.server.module.value.base.converter;

import ru.msvdev.ds.server.module.value.base.StringConverter;

public class BooleanStringConverter implements StringConverter<Boolean> {

    @Override
    public Boolean fromString(String string) {
        return Boolean.valueOf(string);
    }

    @Override
    public String toString(Boolean object) {
        return object.toString();
    }
}
