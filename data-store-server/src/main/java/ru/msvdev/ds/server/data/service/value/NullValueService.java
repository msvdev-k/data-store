package ru.msvdev.ds.server.data.service.value;

import org.springframework.stereotype.Service;
import ru.msvdev.ds.server.utils.type.ConstantValue;
import ru.msvdev.ds.server.utils.type.ValueServiceBeanNames;


@Service(ValueServiceBeanNames.NULL)
public class NullValueService implements ValueService {

    @Override
    public Long put(String valueString) {
        return ConstantValue.NULL.id;
    }

    @Override
    public String get(Long id) {
        return "";
    }
}
