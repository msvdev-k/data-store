package ru.msvdev.ds.server.module.value.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.msvdev.ds.server.module.value.repository.BytesValueRepository;
import ru.msvdev.ds.server.module.value.repository.ValueRepository;
import ru.msvdev.ds.server.module.value.base.ValueServiceBeanNames;
import ru.msvdev.ds.server.module.value.base.converter.BytesStringConverter;
import ru.msvdev.ds.server.module.value.base.StringConverter;


@RequiredArgsConstructor
@Service(ValueServiceBeanNames.BYTES)
public class BytesValueService extends AbstractValueService<String> {

    private final BytesValueRepository repository;
    private final BytesStringConverter stringConverter = new BytesStringConverter();

    @Override
    protected ValueRepository<String> getRepository() {
        return repository;
    }

    @Override
    protected StringConverter<String> getConverter() {
        return stringConverter;
    }

}
