package ru.msvdev.ds.server.data.service.value;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.msvdev.ds.server.data.repository.value.UuidValueRepository;
import ru.msvdev.ds.server.data.repository.value.ValueRepository;
import ru.msvdev.ds.server.utils.type.ValueServiceBeanNames;
import ru.msvdev.ds.server.utils.type.converter.StringConverter;
import ru.msvdev.ds.server.utils.type.converter.UuidStringConverter;

import java.util.UUID;


@RequiredArgsConstructor
@Service(ValueServiceBeanNames.UUID)
public class UuidValueService extends BaseValueService<UUID> {

    private final UuidValueRepository repository;
    private final UuidStringConverter stringConverter = new UuidStringConverter();

    @Override
    protected ValueRepository<UUID> getRepository() {
        return repository;
    }

    @Override
    protected StringConverter<UUID> getConverter() {
        return stringConverter;
    }

}
