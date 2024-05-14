package ru.msvdev.ds.server.service.value;

import ru.msvdev.ds.server.dao.repository.value.ValueRepository;
import ru.msvdev.ds.server.utils.type.converter.StringConverter;


public abstract class BaseValueService<T> implements ValueService {


    protected abstract ValueRepository<T> getRepository();

    protected abstract StringConverter<T> getConverter();


    @Override
    public Long put(String valueString) {
        T value = getConverter().fromString(valueString);

        return getRepository()
                .findIdByValue(value)
                .orElseGet(() -> getRepository().insert(value));
    }


    @Override
    public String get(Long id) {
        return getRepository()
                .findValueById(id)
                .map(getConverter()::toString)
                .orElseThrow();
    }
}
