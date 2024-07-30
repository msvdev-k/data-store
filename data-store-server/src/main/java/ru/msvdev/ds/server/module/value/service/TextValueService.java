package ru.msvdev.ds.server.module.value.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.msvdev.ds.server.module.value.repository.TextValueRepository;
import ru.msvdev.ds.server.module.value.repository.ValueRepository;
import ru.msvdev.ds.server.module.value.base.ValueServiceBeanNames;
import ru.msvdev.ds.server.module.value.base.StringConverter;
import ru.msvdev.ds.server.module.value.base.converter.TextStringConverter;


@RequiredArgsConstructor
@Service(ValueServiceBeanNames.TEXT)
public class TextValueService extends AbstractValueService<String> {

    private final TextValueRepository repository;
    private final TextStringConverter stringConverter = new TextStringConverter();

    @Override
    protected ValueRepository<String> getRepository() {
        return repository;
    }

    @Override
    protected StringConverter<String> getConverter() {
        return stringConverter;
    }

}
