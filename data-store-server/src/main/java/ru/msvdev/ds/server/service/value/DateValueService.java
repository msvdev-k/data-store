package ru.msvdev.ds.server.service.value;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.msvdev.ds.server.dao.repository.value.DateValueRepository;
import ru.msvdev.ds.server.dao.repository.value.ValueRepository;
import ru.msvdev.ds.server.utils.type.ValueServiceBeanNames;
import ru.msvdev.ds.server.utils.type.converter.DateStringConverter;
import ru.msvdev.ds.server.utils.type.converter.StringConverter;

import java.time.LocalDate;


@RequiredArgsConstructor
@Service(ValueServiceBeanNames.DATE)
public class DateValueService extends BaseValueService<LocalDate> {

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
