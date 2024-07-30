package ru.msvdev.ds.server.module.value.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.msvdev.ds.server.module.value.repository.StringValueRepository;
import ru.msvdev.ds.server.module.value.repository.ValueRepository;
import ru.msvdev.ds.server.module.value.base.ValueServiceBeanNames;
import ru.msvdev.ds.server.module.value.base.StringConverter;
import ru.msvdev.ds.server.module.value.base.converter.StringStringConverter;


@RequiredArgsConstructor
@Service(ValueServiceBeanNames.STRING)
public class StringValueService extends AbstractValueService<String> {

    private final StringValueRepository repository;
    private final StringStringConverter stringConverter = new StringStringConverter();

    @Override
    protected ValueRepository<String> getRepository() {
        return repository;
    }

    @Override
    protected StringConverter<String> getConverter() {
        return stringConverter;
    }

}
