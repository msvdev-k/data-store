package ru.msvdev.ds.server.module.card;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import ru.msvdev.ds.server.base.ApplicationTest;
import ru.msvdev.ds.server.module.card.entity.Card;
import ru.msvdev.ds.server.module.card.repository.CardRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@DataJdbcTest
@Sql(
        value = {"classpath:module/card/card-repository-test.sql"},
        config = @SqlConfig(encoding = "UTF8")
)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CardRepositoryTest extends ApplicationTest {

    private final CardRepository cardRepository;

    @Autowired
    public CardRepositoryTest(CardRepository cardRepository) {
        this.cardRepository = cardRepository;
    }


    @Test
    void getCards() {
        // region Given
        long catalogId = 2;
        Card[] cards = new Card[]{
                new Card(16, 2),
                new Card(17, 2)
        };
        // endregion


        // region When
        List<Card> foundCards = cardRepository.getCards(catalogId);
        // endregion


        // region Then
        assertEquals(cards.length, foundCards.size());
        for (Card c : cards) {
            assertTrue(foundCards.contains(c));
        }
        // endregion
    }


    @ParameterizedTest
    @CsvSource({
            "1, 5",
            "2, 2",
            "3, 0"
    })
    void count(long catalogId, int expectedCount) {
        // region Given
        // endregion


        // region When
        int actualCount = cardRepository.count(catalogId);
        // endregion


        // region Then
        assertEquals(expectedCount, actualCount);
        // endregion
    }


    @ParameterizedTest
    @CsvSource({
            "1, 11",
            "1, 12",
            "1, 13",
            "1, 14",
            "1, 15",
            "2, 16",
            "2, 17"
    })
    void existsById(long catalogId, long cardId) {
        // region Given
        // endregion


        // region When
        boolean existFlag = cardRepository.existsById(catalogId, cardId);
        // endregion


        // region Then
        assertTrue(existFlag);
        // endregion
    }


    @Test
    void insert() {
        // region Given
        long catalogId = 1;
        long cardId = 37;
        // endregion


        // region When
        Card insertedCard = cardRepository.insert(catalogId);
        // endregion


        // region Then
        assertNotNull(insertedCard);
        assertEquals(cardId, insertedCard.id());
        assertEquals(catalogId, insertedCard.catalogId());
        // endregion
    }


    @Test
    void deleteById() {
        // region Given
        long catalogId = 2;
        Card[] cards = new Card[]{
                new Card(16, 2),
                new Card(17, 2)
        };
        // endregion


        // region When
        boolean deleteFlag = cardRepository.deleteById(catalogId, cards[0].id());
        // endregion


        // region Then
        assertTrue(deleteFlag);
        assertFalse(cardRepository.existsById(catalogId, cards[0].id()));
        assertTrue(cardRepository.existsById(catalogId, cards[1].id()));
        // endregion
    }

}