package ru.msvdev.ds.server.module.value.service;

import org.springframework.stereotype.Service;
import ru.msvdev.ds.server.module.value.base.ConstantValue;
import ru.msvdev.ds.server.module.value.base.ValueServiceBeanNames;


@Service(ValueServiceBeanNames.NULL)
public class NullValueService implements ValueService {

    @Override
    public Long put(String valueString) {
        return ConstantValue.NULL.id;
    }

    @Override
    public String get(long id) {
        return (id == ConstantValue.NULL.id) ? "" : null;
    }
}
