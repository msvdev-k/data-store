package ru.msvdev.ds.server.dao.repository.value;

import org.springframework.data.jdbc.repository.query.Query;


public interface JsonValueRepository extends ValueRepository<String> {

    @Override
    @Query("""
            WITH jsonb_value AS (
                SELECT :value::JSONB AS value
            )
            SELECT id
            FROM json_values AS t1
            INNER JOIN jsonb_value AS t2 ON t1.value @> t2.value AND t1.value <@ t2.value
            """)
    Long findIdByValue(String value);


    @Override
    @Query("SELECT value FROM json_values WHERE id = :id")
    String findValueById(Long id);


    @Override
    @Query("""
            WITH value_id AS (
                INSERT INTO "values" (id) VALUES (DEFAULT)
                RETURNING id
            ), value AS (
                INSERT INTO json_values (id, value) VALUES ((SELECT id FROM value_id), :value::JSONB)
            )
            SELECT id FROM value_id
            """)
    Long insert(String value);

}
