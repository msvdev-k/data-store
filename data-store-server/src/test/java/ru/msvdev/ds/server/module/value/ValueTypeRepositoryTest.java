package ru.msvdev.ds.server.module.value;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import ru.msvdev.ds.server.base.ApplicationTest;
import ru.msvdev.ds.server.module.value.entity.ValueType;
import ru.msvdev.ds.server.module.value.repository.ValueTypeRepository;
import ru.msvdev.ds.server.openapi.model.FieldTypes;
import ru.msvdev.ds.server.module.value.base.DataType;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


@DataJdbcTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ValueTypeRepositoryTest extends ApplicationTest {

    private final ValueTypeRepository valueTypeRepository;

    @Autowired
    public ValueTypeRepositoryTest(ValueTypeRepository valueTypeRepository) {
        this.valueTypeRepository = valueTypeRepository;
    }


    @Test
    void findAll() {
        // region Given
        DataType[] dataTypes = DataType.values();
        // endregion


        // region When
        List<ValueType> valueTypes = valueTypeRepository.findAll();
        // endregion


        // region Then
        assertEquals(dataTypes.length, valueTypes.size());

        for (DataType type : dataTypes) {
            Optional<ValueType> optional = valueTypes.stream().filter(valueType -> valueType.type() == type).findFirst();
            assertTrue(optional.isPresent());
            assertEquals(type.id, optional.get().id());
        }

        valueTypes.forEach(System.out::println);
        // endregion
    }


    @ParameterizedTest
    @EnumSource(DataType.class)
    void mappingDataTypeToFieldTypeTest(DataType dataType) {
        // region Given
        String type = dataType.name();
        // endregion

        // region Given
        FieldTypes fieldType = FieldTypes.valueOf(type);
        // endregion

        // region Given
        assertEquals(dataType.name(), fieldType.name());
        // endregion
    }


    @ParameterizedTest
    @EnumSource(FieldTypes.class)
    void mappingFieldTypeToDataTypeTest(FieldTypes fieldTypes) {
        // region Given
        String type = fieldTypes.name();
        // endregion

        // region Given
        DataType dataType = DataType.valueOf(type);
        // endregion

        // region Given
        assertEquals(fieldTypes.name(), dataType.name());
        // endregion
    }
}