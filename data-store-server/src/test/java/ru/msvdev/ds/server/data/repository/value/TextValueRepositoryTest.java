package ru.msvdev.ds.server.data.repository.value;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;


class TextValueRepositoryTest extends ValueRepositoryTest {

    private final TextValueRepository textValueRepository;

    @Autowired
    public TextValueRepositoryTest(TextValueRepository textValueRepository) {
        this.textValueRepository = textValueRepository;
    }


    @Test
    void baseTest() {
        String value = "Некоторая строка текста. The English text. Некоторая строка текста!";
        baseFindInsertTest(textValueRepository,value, Assertions::assertEquals);
    }

}