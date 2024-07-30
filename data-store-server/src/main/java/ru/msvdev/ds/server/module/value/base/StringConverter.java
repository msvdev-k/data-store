package ru.msvdev.ds.server.module.value.base;

public interface StringConverter<T> {

    T fromString(String string);

    String toString(T object);
}
