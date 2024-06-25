package ru.msvdev.ds.server.dao.repository.value;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.context.jdbc.Sql;
import ru.msvdev.ds.server.base.ApplicationTest;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;


@DataJdbcTest
@Sql({"classpath:db/repository/value-repository-test.sql"})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class JsonValueRepositoryTest extends ApplicationTest {

    private final JsonValueRepository valueRepository;

    @Autowired
    public JsonValueRepositoryTest(JsonValueRepository valueRepository) {
        this.valueRepository = valueRepository;
    }


    @Test
    void findIdByValue() {
        // region Given
        long id = 30;
        String value = "{\"figure\": [true, \"square\"], \"tags\": {\"a\": 1, \"b\": null}}";
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
        long id = 30;
        // endregion

        // region When
        String valueById = valueRepository.findValueById(id);
        // endregion

        // region Then
        assertNotNull(valueById);
        //endregion
    }


    @Test
    void insert() {
        // region Given
        long id = 37;
        String value = """
                {
                  "array": [1, 2, 3],
                  "boolean": true,
                  "color": "gold",
                  "null": null,
                  "number": 12345,
                  "object": {"a": "b", "c": "d"},
                  "string": "msvdev.ru"
                }
                """;
        // endregion

        // region When
        Long insertedId = valueRepository.insert(value);
        // endregion

        // region Then
        assertEquals(id, insertedId);
        //endregion
    }
}