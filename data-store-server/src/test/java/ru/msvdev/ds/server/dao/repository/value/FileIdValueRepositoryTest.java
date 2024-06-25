package ru.msvdev.ds.server.dao.repository.value;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.context.jdbc.Sql;
import ru.msvdev.ds.server.base.ApplicationTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


@DataJdbcTest
@Sql({"classpath:db/repository/value-repository-test.sql"})
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
        long id = 31;
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
        long id = 31;
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
        long id = 37;
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