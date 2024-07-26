package ru.msvdev.ds.server.module.field;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import ru.msvdev.ds.server.base.ApplicationTest;
import ru.msvdev.ds.server.module.field.entity.FieldType;
import ru.msvdev.ds.server.module.field.repository.FieldTypeRepository;
import ru.msvdev.ds.server.openapi.model.FieldTypes;
import ru.msvdev.ds.server.utils.type.ValueType;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


@DataJdbcTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class FieldTypeRepositoryTest extends ApplicationTest {

    private final FieldTypeRepository fieldTypeRepository;

    @Autowired
    public FieldTypeRepositoryTest(FieldTypeRepository fieldTypeRepository) {
        this.fieldTypeRepository = fieldTypeRepository;
    }


    @Test
    void findAll() {
        // region Given
        ValueType[] valueTypes = ValueType.values();
        // endregion


        // region When
        List<FieldType> fieldTypes = fieldTypeRepository.findAll();
        // endregion


        // region Then
        assertEquals(valueTypes.length, fieldTypes.size());

        for (ValueType type : valueTypes) {
            Optional<FieldType> optional = fieldTypes.stream().filter(fieldType -> fieldType.type() == type).findFirst();
            assertTrue(optional.isPresent());
            assertEquals(type.id, optional.get().id());
        }

        fieldTypes.forEach(System.out::println);
        // endregion
    }


    @ParameterizedTest
    @EnumSource(ValueType.class)
    void typeMappingTest(ValueType valueType) {
        // region Given
        String type = valueType.name();
        // endregion

        // region Given
        FieldTypes fieldType = FieldTypes.valueOf(type);
        // endregion

        // region Given
        assertEquals(valueType.name(), fieldType.name());
        // endregion
    }
}