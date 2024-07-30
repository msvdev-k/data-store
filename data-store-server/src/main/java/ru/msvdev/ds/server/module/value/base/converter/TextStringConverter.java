package ru.msvdev.ds.server.module.value.base.converter;

import ru.msvdev.ds.server.module.value.base.StringConverter;

public class TextStringConverter implements StringConverter<String> {

    @Override
    public String fromString(String string) {
        return string;
    }

    @Override
    public String toString(String object) {
        return object;
    }

}
