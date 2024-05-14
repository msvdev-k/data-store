package ru.msvdev.ds.server.service.value;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.msvdev.ds.server.dao.repository.value.IntegerValueRepository;
import ru.msvdev.ds.server.dao.repository.value.ValueRepository;
import ru.msvdev.ds.server.utils.type.ValueServiceBeanNames;
import ru.msvdev.ds.server.utils.type.converter.IntegerStringConverter;
import ru.msvdev.ds.server.utils.type.converter.StringConverter;


@RequiredArgsConstructor
@Service(ValueServiceBeanNames.INTEGER)
public class IntegerValueService extends BaseValueService<Long> {

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
