package ru.msvdev.ds.server.module.value.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.msvdev.ds.server.module.value.repository.JsonValueRepository;
import ru.msvdev.ds.server.module.value.repository.ValueRepository;
import ru.msvdev.ds.server.module.value.base.ValueServiceBeanNames;
import ru.msvdev.ds.server.module.value.base.converter.JsonStringConverter;
import ru.msvdev.ds.server.module.value.base.StringConverter;


@RequiredArgsConstructor
@Service(ValueServiceBeanNames.JSON)
public class JsonValueService extends AbstractValueService<String> {

    private final JsonValueRepository repository;
    private final JsonStringConverter stringConverter = new JsonStringConverter();

    @Override
    protected ValueRepository<String> getRepository() {
        return repository;
    }

    @Override
    protected StringConverter<String> getConverter() {
        return stringConverter;
    }

}
