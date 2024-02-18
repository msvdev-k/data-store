package ru.msvdev.ds.server.data.service.value;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.msvdev.ds.server.data.repository.value.StringValueRepository;
import ru.msvdev.ds.server.data.repository.value.ValueRepository;
import ru.msvdev.ds.server.utils.type.ValueServiceBeanNames;
import ru.msvdev.ds.server.utils.type.converter.StringConverter;
import ru.msvdev.ds.server.utils.type.converter.StringStringConverter;


@RequiredArgsConstructor
@Service(ValueServiceBeanNames.STRING)
public class StringValueService extends BaseValueService<String> {

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
