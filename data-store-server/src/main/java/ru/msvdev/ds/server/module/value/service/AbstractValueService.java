package ru.msvdev.ds.server.module.value.service;

import ru.msvdev.ds.server.module.value.repository.ValueRepository;
import ru.msvdev.ds.server.module.value.base.StringConverter;


public abstract class AbstractValueService<T> implements ValueService {


    protected abstract ValueRepository<T> getRepository();

    protected abstract StringConverter<T> getConverter();


    @Override
    public Long put(String valueString) {
        T value = getConverter().fromString(valueString);
        Long valueId = getRepository().findIdByValue(value);
        return (valueId != null) ? valueId : getRepository().insert(value);
    }


    @Override
    public String get(long id) {
        T value = getRepository().findValueById(id);
        return (value != null) ? getConverter().toString(value) : null;
    }
}
