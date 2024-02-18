package ru.msvdev.ds.server.utils.type.converter;

import java.time.LocalDate;

public class DateStringConverter implements StringConverter<LocalDate> {

    @Override
    public LocalDate fromString(String string) {
        return LocalDate.parse(string);
    }

    @Override
    public String toString(LocalDate object) {
        return object.toString();
    }

}
