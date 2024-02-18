package ru.msvdev.ds.server.utils.type.converter;

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
