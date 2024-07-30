package ru.msvdev.ds.server.module.value.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.msvdev.ds.server.module.value.repository.BigDecimalValueRepository;
import ru.msvdev.ds.server.module.value.repository.ValueRepository;
import ru.msvdev.ds.server.module.value.base.ValueServiceBeanNames;
import ru.msvdev.ds.server.module.value.base.converter.BigDecimelStringConverter;
import ru.msvdev.ds.server.module.value.base.StringConverter;

import java.math.BigDecimal;


@RequiredArgsConstructor
@Service(ValueServiceBeanNames.BIG_DECIMAL)
public class BigDecimalValueService extends AbstractValueService<BigDecimal> {

    private final BigDecimalValueRepository repository;
    private final BigDecimelStringConverter stringConverter = new BigDecimelStringConverter();

    @Override
    protected ValueRepository<BigDecimal> getRepository() {
        return repository;
    }

    @Override
    protected StringConverter<BigDecimal> getConverter() {
        return stringConverter;
    }

}
