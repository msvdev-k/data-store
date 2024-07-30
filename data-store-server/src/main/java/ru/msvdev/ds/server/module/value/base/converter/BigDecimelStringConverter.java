package ru.msvdev.ds.server.module.value.base.converter;

import ru.msvdev.ds.server.module.value.base.StringConverter;

import java.math.BigDecimal;

public class BigDecimelStringConverter implements StringConverter<BigDecimal> {

    @Override
    public BigDecimal fromString(String string) {
        return new BigDecimal(string);
    }

    @Override
    public String toString(BigDecimal object) {
        return object.toString();
    }
}
