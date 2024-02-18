package ru.msvdev.ds.server.data.repository.value;

import org.springframework.data.jdbc.repository.query.Query;

import java.util.Optional;


public interface StringValueRepository extends ValueRepository<String> {

    @Override
    @Query("SELECT id FROM string_values WHERE value = :value")
    Optional<Long> findIdByValue(String value);


    @Override
    @Query("SELECT value FROM string_values WHERE id = :id")
    Optional<String> findValueById(Long id);


    @Override
    @Query("""
            WITH value_id AS (
                INSERT INTO "values" (id) VALUES (DEFAULT)
                RETURNING id
            ), value AS (
                INSERT INTO string_values (id, value) VALUES ((SELECT id FROM value_id), :value)
            )
            SELECT id FROM value_id
            """)
    Long insert(String value);

}
