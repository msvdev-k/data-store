package ru.msvdev.ds.server.module.value.base.converter;

import ru.msvdev.ds.server.module.value.base.StringConverter;

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
