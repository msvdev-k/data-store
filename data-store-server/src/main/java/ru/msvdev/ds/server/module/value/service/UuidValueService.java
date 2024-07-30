package ru.msvdev.ds.server.module.value.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.msvdev.ds.server.module.value.repository.UuidValueRepository;
import ru.msvdev.ds.server.module.value.repository.ValueRepository;
import ru.msvdev.ds.server.module.value.base.ValueServiceBeanNames;
import ru.msvdev.ds.server.module.value.base.StringConverter;
import ru.msvdev.ds.server.module.value.base.converter.UuidStringConverter;

import java.util.UUID;


@RequiredArgsConstructor
@Service(ValueServiceBeanNames.UUID)
public class UuidValueService extends AbstractValueService<UUID> {

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
