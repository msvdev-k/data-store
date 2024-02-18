package ru.msvdev.ds.server.data.repository.value;

import org.springframework.data.jdbc.repository.query.Query;

import java.util.Optional;


public interface TextValueRepository extends ValueRepository<String> {

    @Override
    @Query("SELECT id FROM text_values WHERE value = :value")
    Optional<Long> findIdByValue(String value);


    @Override
    @Query("SELECT value FROM text_values WHERE id = :id")
    Optional<String> findValueById(Long id);


    @Override
    @Query("""
            WITH value_id AS (
                INSERT INTO "values" (id) VALUES (DEFAULT)
                RETURNING id
            ), value AS (
                INSERT INTO text_values (id, value, vector) VALUES ((SELECT id FROM value_id), :value, to_tsvector(:value))
            )
            SELECT id FROM value_id
            """)
    Long insert(String value);

}
