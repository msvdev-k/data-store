package ru.msvdev.ds.server.service.value;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.msvdev.ds.server.dao.repository.value.BytesValueRepository;
import ru.msvdev.ds.server.dao.repository.value.ValueRepository;
import ru.msvdev.ds.server.utils.type.ValueServiceBeanNames;
import ru.msvdev.ds.server.utils.type.converter.BytesStringConverter;
import ru.msvdev.ds.server.utils.type.converter.StringConverter;


@RequiredArgsConstructor
@Service(ValueServiceBeanNames.BYTES)
public class BytesValueService extends BaseValueService<String> {

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
