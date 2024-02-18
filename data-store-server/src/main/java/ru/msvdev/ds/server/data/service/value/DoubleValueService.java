package ru.msvdev.ds.server.data.service.value;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.msvdev.ds.server.data.repository.value.DoubleValueRepository;
import ru.msvdev.ds.server.data.repository.value.ValueRepository;
import ru.msvdev.ds.server.utils.type.ValueServiceBeanNames;
import ru.msvdev.ds.server.utils.type.converter.DoubleStringConverter;
import ru.msvdev.ds.server.utils.type.converter.StringConverter;


@RequiredArgsConstructor
@Service(ValueServiceBeanNames.DOUBLE)
public class DoubleValueService extends BaseValueService<Double> {

    private final DoubleValueRepository repository;
    private final DoubleStringConverter stringConverter = new DoubleStringConverter();


    @Override
    protected ValueRepository<Double> getRepository() {
        return repository;
    }

    @Override
    protected StringConverter<Double> getConverter() {
        return stringConverter;
    }

}
