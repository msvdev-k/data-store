package ru.msvdev.ds.server.module.value.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.msvdev.ds.server.module.value.repository.DoubleValueRepository;
import ru.msvdev.ds.server.module.value.repository.ValueRepository;
import ru.msvdev.ds.server.module.value.base.ValueServiceBeanNames;
import ru.msvdev.ds.server.module.value.base.converter.DoubleStringConverter;
import ru.msvdev.ds.server.module.value.base.StringConverter;


@RequiredArgsConstructor
@Service(ValueServiceBeanNames.DOUBLE)
public class DoubleValueService extends AbstractValueService<Double> {

    private final DoubleValueRepository repository;
    private final DoubleStringConverter stringConverter = new DoubleStringConverter();


    @Override
    protected ValueRepository<Double> getRepository() {
        return repository;
    }

    @Override
    protected StringConverter<Double> getConverter() {
        return stringConverter;
    }

}
