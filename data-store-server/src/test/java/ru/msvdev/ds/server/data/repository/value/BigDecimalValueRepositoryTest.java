package ru.msvdev.ds.server.data.repository.value;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;


class BigDecimalValueRepositoryTest extends ValueRepositoryTest {

    private final BigDecimalValueRepository bigDecimalValueRepository;

    @Autowired
    public BigDecimalValueRepositoryTest(BigDecimalValueRepository bigDecimalValueRepository) {
        this.bigDecimalValueRepository = bigDecimalValueRepository;
    }


    @Test
    void baseTest() {
        BigDecimal value = BigDecimal.valueOf(32452840.456);
        baseFindInsertTest(bigDecimalValueRepository, value, Assertions::assertEquals);
    }

}