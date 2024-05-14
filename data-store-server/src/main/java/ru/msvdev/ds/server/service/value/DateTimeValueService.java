package ru.msvdev.ds.server.service.value;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.msvdev.ds.server.dao.repository.value.DateTimeValueRepository;
import ru.msvdev.ds.server.dao.repository.value.ValueRepository;
import ru.msvdev.ds.server.utils.type.ValueServiceBeanNames;
import ru.msvdev.ds.server.utils.type.converter.DateTimeStringConverter;
import ru.msvdev.ds.server.utils.type.converter.StringConverter;

import java.time.OffsetDateTime;


@RequiredArgsConstructor
@Service(ValueServiceBeanNames.DATETIME)
public class DateTimeValueService extends BaseValueService<OffsetDateTime> {

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
