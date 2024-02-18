package ru.msvdev.ds.server.utils.type.converter;

public interface StringConverter<T> {

    T fromString(String string);

    String toString(T object);
}
