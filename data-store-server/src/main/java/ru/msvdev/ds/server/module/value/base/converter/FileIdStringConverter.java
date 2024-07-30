package ru.msvdev.ds.server.module.value.base.converter;

import ru.msvdev.ds.server.module.value.base.StringConverter;

public class FileIdStringConverter implements StringConverter<Long> {

    @Override
    public Long fromString(String string) {
        return Long.valueOf(string);
    }

    @Override
    public String toString(Long object) {
        return object.toString();
    }

}
