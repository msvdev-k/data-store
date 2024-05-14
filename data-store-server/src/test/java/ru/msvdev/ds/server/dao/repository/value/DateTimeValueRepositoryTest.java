package ru.msvdev.ds.server.dao.repository.value;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.OffsetDateTime;


class DateTimeValueRepositoryTest extends ValueRepositoryTest {

    private final DateTimeValueRepository dateTimeValueRepository;

    @Autowired
    public DateTimeValueRepositoryTest(DateTimeValueRepository dateTimeValueRepository) {
        this.dateTimeValueRepository = dateTimeValueRepository;
    }


    @Test
    void baseTest() {
        OffsetDateTime value = OffsetDateTime.now().plusYears(100);
        baseFindInsertTest(dateTimeValueRepository, value);
    }

}
