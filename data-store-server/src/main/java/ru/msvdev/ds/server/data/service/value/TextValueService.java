package ru.msvdev.ds.server.data.service.value;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.msvdev.ds.server.data.repository.value.TextValueRepository;
import ru.msvdev.ds.server.data.repository.value.ValueRepository;
import ru.msvdev.ds.server.utils.type.ValueServiceBeanNames;
import ru.msvdev.ds.server.utils.type.converter.StringConverter;
import ru.msvdev.ds.server.utils.type.converter.TextStringConverter;


@RequiredArgsConstructor
@Service(ValueServiceBeanNames.TEXT)
public class TextValueService extends BaseValueService<String> {

    private final TextValueRepository repository;
    private final TextStringConverter stringConverter = new TextStringConverter();

    @Override
    protected ValueRepository<String> getRepository() {
        return repository;
    }

    @Override
    protected StringConverter<String> getConverter() {
        return stringConverter;
    }

}
