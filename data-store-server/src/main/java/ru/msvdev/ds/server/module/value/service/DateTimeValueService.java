package ru.msvdev.ds.server.module.value.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.msvdev.ds.server.module.value.repository.DateTimeValueRepository;
import ru.msvdev.ds.server.module.value.repository.ValueRepository;
import ru.msvdev.ds.server.module.value.base.ValueServiceBeanNames;
import ru.msvdev.ds.server.module.value.base.converter.DateTimeStringConverter;
import ru.msvdev.ds.server.module.value.base.StringConverter;

import java.time.OffsetDateTime;


@RequiredArgsConstructor
@Service(ValueServiceBeanNames.DATETIME)
public class DateTimeValueService extends AbstractValueService<OffsetDateTime> {

    private final DateTimeValueRepository repository;
    private final DateTimeStringConverter stringConverter = new DateTimeStringConverter();


    @Override
    protected ValueRepository<OffsetDateTime> getRepository() {
        return repository;
    }

    @Override
    protected StringConverter<OffsetDateTime> getConverter() {
        return stringConverter;
    }

}
