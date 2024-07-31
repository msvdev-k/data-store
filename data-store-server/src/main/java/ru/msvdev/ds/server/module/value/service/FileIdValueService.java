package ru.msvdev.ds.server.module.value.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.msvdev.ds.server.module.value.base.StringConverter;
import ru.msvdev.ds.server.module.value.base.ValueServiceBeanNames;
import ru.msvdev.ds.server.module.value.base.converter.FileIdStringConverter;
import ru.msvdev.ds.server.module.value.repository.FileIdValueRepository;
import ru.msvdev.ds.server.module.value.repository.ValueRepository;


@RequiredArgsConstructor
@Service(ValueServiceBeanNames.FILE_ID)
public class FileIdValueService extends AbstractValueService<Long> {

    private final FileIdValueRepository repository;
    private final FileIdStringConverter stringConverter = new FileIdStringConverter();


    @Override
    protected ValueRepository<Long> getRepository() {
        return repository;
    }

    @Override
    protected StringConverter<Long> getConverter() {
        return stringConverter;
    }

}
