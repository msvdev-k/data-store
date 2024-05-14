package ru.msvdev.ds.server.dao.repository.value;

import org.springframework.data.jdbc.repository.query.Query;

import java.time.OffsetDateTime;
import java.util.Optional;


public interface DateTimeValueRepository extends ValueRepository<OffsetDateTime> {

    @Override
    @Query("SELECT id FROM datetime_values WHERE value = :value")
    Optional<Long> findIdByValue(OffsetDateTime value);


    @Override
    @Query("SELECT value FROM datetime_values WHERE id = :id")
    Optional<OffsetDateTime> findValueById(Long id);


    @Override
    @Query("""
            WITH value_id AS (
                INSERT INTO "values" (id) VALUES (DEFAULT)
                RETURNING id
            ), value AS (
                INSERT INTO datetime_values (id, value) VALUES ((SELECT id FROM value_id), :value)
            )
            SELECT id FROM value_id
            """)
    Long insert(OffsetDateTime value);

}
