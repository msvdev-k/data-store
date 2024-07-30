package ru.msvdev.ds.server.module.value.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.msvdev.ds.server.module.value.repository.IntegerValueRepository;
import ru.msvdev.ds.server.module.value.repository.ValueRepository;
import ru.msvdev.ds.server.module.value.base.ValueServiceBeanNames;
import ru.msvdev.ds.server.module.value.base.converter.IntegerStringConverter;
import ru.msvdev.ds.server.module.value.base.StringConverter;


@RequiredArgsConstructor
@Service(ValueServiceBeanNames.INTEGER)
public class IntegerValueService extends AbstractValueService<Long> {

    private final IntegerValueRepository repository;
    private final IntegerStringConverter stringConverter = new IntegerStringConverter();


    @Override
    protected ValueRepository<Long> getRepository() {
        return repository;
    }

    @Override
    protected StringConverter<Long> getConverter() {
        return stringConverter;
    }

}
