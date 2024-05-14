package ru.msvdev.ds.server.service.value;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.msvdev.ds.server.dao.repository.value.JsonValueRepository;
import ru.msvdev.ds.server.dao.repository.value.ValueRepository;
import ru.msvdev.ds.server.utils.type.ValueServiceBeanNames;
import ru.msvdev.ds.server.utils.type.converter.JsonStringConverter;
import ru.msvdev.ds.server.utils.type.converter.StringConverter;


@RequiredArgsConstructor
@Service(ValueServiceBeanNames.JSON)
public class JsonValueService extends BaseValueService<String> {

    private final JsonValueRepository repository;
    private final JsonStringConverter stringConverter = new JsonStringConverter();

    @Override
    protected ValueRepository<String> getRepository() {
        return repository;
    }

    @Override
    protected StringConverter<String> getConverter() {
        return stringConverter;
    }

}
