package ru.msvdev.ds.server.service.value;

import ru.msvdev.ds.server.dao.repository.value.ValueRepository;
import ru.msvdev.ds.server.utils.type.converter.StringConverter;


public abstract class BaseValueService<T> implements ValueService {


    protected abstract ValueRepository<T> getRepository();

    protected abstract StringConverter<T> getConverter();


    @Override
    public Long put(String valueString) {
        T value = getConverter().fromString(valueString);
        Long valueId = getRepository().findIdByValue(value);
        return (valueId != null) ? valueId : getRepository().insert(value);
    }


    @Override
    public String get(Long id) {
        T value = getRepository().findValueById(id);
        if (value == null) throw new RuntimeException();
        return getConverter().toString(value);
    }
}
