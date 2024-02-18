package ru.msvdev.ds.server.data.repository.value;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;


class DoubleValueRepositoryTest extends ValueRepositoryTest {

    private final DoubleValueRepository doubleValueRepository;

    @Autowired
    public DoubleValueRepositoryTest(DoubleValueRepository doubleValueRepository) {
        this.doubleValueRepository = doubleValueRepository;
    }


    @Test
    void baseTest() {
        double value = 3.14e-128;
        baseFindInsertTest(doubleValueRepository, value, Assertions::assertEquals);
    }

}