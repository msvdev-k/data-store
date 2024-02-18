package ru.msvdev.ds.server.data.repository.value;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


class JsonValueRepositoryTest extends ValueRepositoryTest {

    private final JsonValueRepository jsonValueRepository;

    @Autowired
    public JsonValueRepositoryTest(JsonValueRepository jsonValueRepository) {
        this.jsonValueRepository = jsonValueRepository;
    }


    @Test
    void baseTest() {
        String value1 = """
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

        String value2 = """
                {
                  "string": "msvdev.ru",
                  "array": [3, 1, 2],
                  "color": "gold",
                  "boolean": true,
                  "null": null,
                  "number": 12345,
                  "object": {
                            "c": "d", "a": "b"
                            }
                }
                """;
        System.out.println(value1);
        System.out.println(value2);

        baseFindInsertTest(jsonValueRepository, value1);

        // = findIdByValue ==============

        Optional<Long> foundId1 = jsonValueRepository.findIdByValue(value1);
        assertTrue(foundId1.isPresent());

        Optional<Long> foundId2 = jsonValueRepository.findIdByValue(value2);
        assertTrue(foundId2.isPresent());

        assertEquals(foundId1.get(), foundId2.get());
    }

}