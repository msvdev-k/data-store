package ru.msvdev.ds.server.data.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import ru.msvdev.ds.server.base.ApplicationTest;
import ru.msvdev.ds.server.data.entity.FieldType;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;


@DataJdbcTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class FieldTypeRepositoryTest extends ApplicationTest {

    private final FieldTypeRepository fieldTypeRepository;

    @Autowired
    public FieldTypeRepositoryTest(FieldTypeRepository fieldTypeRepository) {
        this.fieldTypeRepository = fieldTypeRepository;
    }

    @Test
    void findAllTest() {
        List<FieldType> fieldTypes = fieldTypeRepository.findAll();

        assertEquals(13, fieldTypes.size());

        for (FieldType fieldType : fieldTypes) {
            assertEquals(fieldType.id(), fieldType.type().id);
        }

        fieldTypes.forEach(System.out::println);
    }
}