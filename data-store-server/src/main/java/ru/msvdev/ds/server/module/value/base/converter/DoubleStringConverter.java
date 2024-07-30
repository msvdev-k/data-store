package ru.msvdev.ds.server.module.value.base.converter;

import ru.msvdev.ds.server.module.value.base.StringConverter;

public class DoubleStringConverter implements StringConverter<Double> {

    @Override
    public Double fromString(String string) {
        return Double.valueOf(string);
    }

    @Override
    public String toString(Double object) {
        return object.toString();
    }

}
