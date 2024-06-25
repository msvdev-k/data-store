package ru.msvdev.ds.server.dao.repository.value;

import org.springframework.data.jdbc.repository.query.Query;


public interface TextValueRepository extends ValueRepository<String> {

    @Override
    @Query("SELECT id FROM text_values WHERE value = :value")
    Long findIdByValue(String value);


    @Override
    @Query("SELECT value FROM text_values WHERE id = :id")
    String findValueById(Long id);


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
