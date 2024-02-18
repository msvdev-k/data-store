package ru.msvdev.ds.server.utils.type.converter;

public class IntegerStringConverter implements StringConverter<Long>{

    @Override
    public Long fromString(String string) {
        return Long.valueOf(string);
    }

    @Override
    public String toString(Long object) {
        return object.toString();
    }

}
