package ru.msvdev.ds.server.utils.type.converter;

public class JsonStringConverter implements StringConverter<String>{

    @Override
    public String fromString(String string) {
        return string;
    }

    @Override
    public String toString(String object) {
        return object;
    }

}
