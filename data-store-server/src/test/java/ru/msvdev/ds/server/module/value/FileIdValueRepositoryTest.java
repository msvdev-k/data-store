package ru.msvdev.ds.server.module.value;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import ru.msvdev.ds.server.base.ApplicationTest;
import ru.msvdev.ds.server.module.value.repository.FileIdValueRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


@DataJdbcTest
@Sql(
        value = {"classpath:module/value/value-repository-test.sql"},
        config = @SqlConfig(encoding = "UTF8")
)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class FileIdValueRepositoryTest extends ApplicationTest {

    private final FileIdValueRepository valueRepository;

    @Autowired
    public FileIdValueRepositoryTest(FileIdValueRepository valueRepository) {
        this.valueRepository = valueRepository;
    }


    @Test
    void findIdByValue() {
        // region Given
        long id = 131;
        long value = 1;
        // endregion

        // region When
        Long idByValue = valueRepository.findIdByValue(value);
        // endregion

        // region Then
        assertNotNull(idByValue);
        assertEquals(id, idByValue);
        //endregion
    }


    @Test
    void findValueById() {
        // region Given
        long id = 131;
        long value = 1;
        // endregion

        // region When
        Long valueById = valueRepository.findValueById(id);
        // endregion

        // region Then
        assertNotNull(valueById);
        assertEquals(value, valueById);
        //endregion
    }


    @Test
    void insert() {
        // region Given
        long id = 137;
        long value = 2;
        // endregion

        // region When
        Long insertedId = valueRepository.insert(value);
        // endregion

        // region Then
        assertEquals(id, insertedId);

        Long valueById = valueRepository.findValueById(insertedId);
        assertNotNull(valueById);
        assertEquals(value, valueById);
        //endregion
    }


}