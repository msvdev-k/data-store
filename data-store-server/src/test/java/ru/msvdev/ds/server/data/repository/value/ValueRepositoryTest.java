package ru.msvdev.ds.server.data.repository.value;

import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import ru.msvdev.ds.server.base.ApplicationTest;

import java.util.Optional;
import java.util.function.BiConsumer;

import static org.junit.jupiter.api.Assertions.*;


//@Commit
@DataJdbcTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ValueRepositoryTest extends ApplicationTest {


    <V> void baseFindInsertTest(ValueRepository<V> repository, V value) {
        baseFindInsertTest(repository, value, null);
    }

    <V> void baseFindInsertTest(ValueRepository<V> repository, V value, BiConsumer<V, V> valueComparator) {

        // = insert =====================

        Long valueId = repository.insert(value);
        assertNotNull(valueId);


        // = findIdByValue ==============

        Optional<Long> foundId = repository.findIdByValue(value);
        assertTrue(foundId.isPresent());
        assertEquals(valueId, foundId.get());


        // = findValueById ==============

        Optional<V> foundValue = repository.findValueById(valueId);
        assertTrue(foundValue.isPresent());
        if (valueComparator != null) {
            valueComparator.accept(value, foundValue.get());
        }
    }

}