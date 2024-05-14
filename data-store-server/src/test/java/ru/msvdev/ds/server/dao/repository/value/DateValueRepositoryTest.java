package ru.msvdev.ds.server.dao.repository.value;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;


class DateValueRepositoryTest extends ValueRepositoryTest {

    private final DateValueRepository dateValueRepository;

    @Autowired
    public DateValueRepositoryTest(DateValueRepository dateValueRepository) {
        this.dateValueRepository = dateValueRepository;
    }


    @Test
    void baseTest() {
        LocalDate value = LocalDate.now().plusYears(100);
        baseFindInsertTest(dateValueRepository, value, Assertions::assertEquals);
    }

}