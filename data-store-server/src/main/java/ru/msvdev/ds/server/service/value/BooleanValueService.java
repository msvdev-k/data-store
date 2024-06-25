package ru.msvdev.ds.server.service.value;

import org.springframework.stereotype.Service;
import ru.msvdev.ds.server.dao.repository.value.ValueRepository;
import ru.msvdev.ds.server.utils.type.ConstantValue;
import ru.msvdev.ds.server.utils.type.ValueServiceBeanNames;
import ru.msvdev.ds.server.utils.type.converter.BooleanStringConverter;
import ru.msvdev.ds.server.utils.type.converter.StringConverter;


@Service(ValueServiceBeanNames.BOOLEAN)
public class BooleanValueService extends BaseValueService<Boolean> {

    private final BooleanStringConverter stringConverter = new BooleanStringConverter();
    private final ValueRepository<Boolean> repository = new ValueRepository<>() {
        @Override
        public Long findIdByValue(Boolean value) {
            return (value) ? ConstantValue.TRUE.id : ConstantValue.FALSE.id;
        }

        @Override
        public Boolean findValueById(Long id) {
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
