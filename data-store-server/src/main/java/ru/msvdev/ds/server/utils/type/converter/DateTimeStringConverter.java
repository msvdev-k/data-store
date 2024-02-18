package ru.msvdev.ds.server.utils.type.converter;

import java.time.OffsetDateTime;

public class DateTimeStringConverter implements StringConverter<OffsetDateTime> {

    @Override
    public OffsetDateTime fromString(String string) {
        return OffsetDateTime.parse(string);
    }

    @Override
    public String toString(OffsetDateTime object) {
        return object.toString();
    }

}
