package ru.msvdev.ds.server.data.service.value;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.msvdev.ds.server.data.repository.value.BigDecimalValueRepository;
import ru.msvdev.ds.server.data.repository.value.ValueRepository;
import ru.msvdev.ds.server.utils.type.ValueServiceBeanNames;
import ru.msvdev.ds.server.utils.type.converter.BigDecimelStringConverter;
import ru.msvdev.ds.server.utils.type.converter.StringConverter;

import java.math.BigDecimal;


@RequiredArgsConstructor
@Service(ValueServiceBeanNames.BIG_DECIMAL)
public class BigDecimalValueService extends BaseValueService<BigDecimal> {

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
