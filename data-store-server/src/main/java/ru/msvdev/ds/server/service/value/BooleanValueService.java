package ru.msvdev.ds.server.service.value;

import org.springframework.stereotype.Service;
import ru.msvdev.ds.server.dao.repository.value.ValueRepository;
import ru.msvdev.ds.server.utils.type.ConstantValue;
import ru.msvdev.ds.server.utils.type.ValueServiceBeanNames;
import ru.msvdev.ds.server.utils.type.converter.BooleanStringConverter;
import ru.msvdev.ds.server.utils.type.converter.StringConverter;

import java.util.Optional;


@Service(ValueServiceBeanNames.BOOLEAN)
public class BooleanValueService extends BaseValueService<Boolean> {

    private final BooleanStringConverter stringConverter = new BooleanStringConverter();
    private final ValueRepository<Boolean> repository = new ValueRepository<>() {
        @Override
        public Optional<Long> findIdByValue(Boolean value) {
            if (value) return Optional.of(ConstantValue.TRUE.id);
            return Optional.of(ConstantValue.FALSE.id);
        }

        @Override
        public Optional<Boolean> findValueById(Long id) {
            if (id == ConstantValue.TRUE.id) return Optional.of(true);
            if (id == ConstantValue.FALSE.id) return Optional.of(false);
            return Optional.empty();
        }

        @Override
        public Long insert(Boolean value) {
            if (value) return ConstantValue.TRUE.id;
            return ConstantValue.FALSE.id;
        }
    };


    @Override
    protected ValueRepository<Boolean> getRepository() {
        return repository;
    }

    @Override
    protected StringConverter<Boolean> getConverter() {
        return stringConverter;
    }

}
