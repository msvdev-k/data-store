package ru.msvdev.ds.server.module.value.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.msvdev.ds.server.module.value.repository.DateValueRepository;
import ru.msvdev.ds.server.module.value.repository.ValueRepository;
import ru.msvdev.ds.server.module.value.base.ValueServiceBeanNames;
import ru.msvdev.ds.server.module.value.base.converter.DateStringConverter;
import ru.msvdev.ds.server.module.value.base.StringConverter;

import java.time.LocalDate;


@RequiredArgsConstructor
@Service(ValueServiceBeanNames.DATE)
public class DateValueService extends AbstractValueService<LocalDate> {

    private final DateValueRepository repository;
    private final DateStringConverter stringConverter = new DateStringConverter();


    @Override
    protected ValueRepository<LocalDate> getRepository() {
        return repository;
    }

    @Override
    protected StringConverter<LocalDate> getConverter() {
        return stringConverter;
    }

}
