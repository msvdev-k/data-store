package ru.msvdev.ds.server.dao.repository.value;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;


class UuidValueRepositoryTest extends ValueRepositoryTest {

    private final UuidValueRepository uuidValueRepository;

    @Autowired
    public UuidValueRepositoryTest(UuidValueRepository uuidValueRepository) {
        this.uuidValueRepository = uuidValueRepository;
    }


    @Test
    void baseTest() {
        UUID value = UUID.randomUUID();
        baseFindInsertTest(uuidValueRepository, value, Assertions::assertEquals);
    }

}