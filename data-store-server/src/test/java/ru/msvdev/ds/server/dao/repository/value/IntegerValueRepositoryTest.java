package ru.msvdev.ds.server.dao.repository.value;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;


class IntegerValueRepositoryTest extends ValueRepositoryTest {

    private final IntegerValueRepository integerValueRepository;

    @Autowired
    public IntegerValueRepositoryTest(IntegerValueRepository integerValueRepository) {
        this.integerValueRepository = integerValueRepository;
    }


    @Test
    void baseTest() {
        long value = 32452840L;
        baseFindInsertTest(integerValueRepository, value, Assertions::assertEquals);
    }

}