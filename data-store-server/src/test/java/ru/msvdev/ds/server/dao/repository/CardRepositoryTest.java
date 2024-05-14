package ru.msvdev.ds.server.dao.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import ru.msvdev.ds.server.base.ApplicationTest;
import ru.msvdev.ds.server.dao.entity.Card;
import ru.msvdev.ds.server.dao.entity.Catalog;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@DataJdbcTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CardRepositoryTest extends ApplicationTest {

    private final CatalogRepository catalogRepository;
    private final CardRepository cardRepository;

    @Autowired
    public CardRepositoryTest(CatalogRepository catalogRepository, CardRepository cardRepository) {
        this.catalogRepository = catalogRepository;
        this.cardRepository = cardRepository;
    }

    private Catalog catalog;

    @BeforeEach
    void setUp() {
        catalog = catalogRepository.insert("Каталог", null);
    }

    @AfterEach
    void tearDown() {
    }


    @Test
    void crudTest() {

        // = insert =====================

        Card card = cardRepository.insert(catalog.id());

        assertTrue(card.id() > 0);
        assertEquals(catalog.id(), card.catalogId());

        // = count ======================
        assertEquals(1, cardRepository.count(catalog.id()));

        // = insert =====================

        List<Card> cards = cardRepository.getCards(catalog.id());

        assertFalse(cards.isEmpty());
        assertTrue(cards.contains(card));


        // = existsById =================

        assertTrue(cardRepository.existsById(catalog.id(), card.id()));
        assertFalse(cardRepository.existsById(catalog.id(), 0));

        // = deleteById =================

        assertTrue(cardRepository.deleteById(catalog.id(), card.id()));
        assertFalse(cardRepository.existsById(catalog.id(), card.id()));
        assertEquals(0, cardRepository.count(catalog.id()));
    }

}