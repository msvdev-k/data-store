package ru.msvdev.ds.server.module.value.service;

import org.springframework.stereotype.Service;
import ru.msvdev.ds.server.module.value.repository.ValueRepository;
import ru.msvdev.ds.server.module.value.base.ConstantValue;
import ru.msvdev.ds.server.module.value.base.ValueServiceBeanNames;
import ru.msvdev.ds.server.module.value.base.converter.BooleanStringConverter;
import ru.msvdev.ds.server.module.value.base.StringConverter;


@Service(ValueServiceBeanNames.BOOLEAN)
public class BooleanValueService extends AbstractValueService<Boolean> {

    private final BooleanStringConverter stringConverter = new BooleanStringConverter();
    private final ValueRepository<Boolean> repository = new ValueRepository<>() {
        @Override
        public Long findIdByValue(Boolean value) {
            return (value) ? ConstantValue.TRUE.id : ConstantValue.FALSE.id;
        }

        @Override
        public Boolean findValueById(long id) {
            if (id == ConstantValue.TRUE.id) return true;
            if (id == ConstantValue.FALSE.id) return false;
            return null;
        }

        @Override
        public Long insert(Boolean value) {
            return (value) ? ConstantValue.TRUE.id : ConstantValue.FALSE.id;
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
