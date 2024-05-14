package ru.msvdev.ds.server.dao.repository.value;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.security.SecureRandom;
import java.util.Base64;


class BytesValueRepositoryTest extends ValueRepositoryTest {

    private final BytesValueRepository bytesValueRepository;

    @Autowired
    public BytesValueRepositoryTest(BytesValueRepository bytesValueRepository) {
        this.bytesValueRepository = bytesValueRepository;
    }


    @Test
    void baseTest() {
        byte[] bytes = new byte[128];
        SecureRandom random = new SecureRandom();
        random.nextBytes(bytes);

        String value = Base64.getEncoder().encodeToString(bytes);
        baseFindInsertTest(bytesValueRepository, value, Assertions::assertEquals);
    }

}