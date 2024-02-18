package ru.msvdev.ds.server.data.repository.value;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.OffsetDateTime;


class StringValueRepositoryTest extends ValueRepositoryTest {

    private final StringValueRepository stringValueRepository;

    @Autowired
    public StringValueRepositoryTest(StringValueRepository stringValueRepository) {
        this.stringValueRepository = stringValueRepository;
    }


    @Test
    void baseTest() {
        String value = "Строка сообщения. " + OffsetDateTime.now();
        baseFindInsertTest(stringValueRepository, value, Assertions::assertEquals);
    }

}