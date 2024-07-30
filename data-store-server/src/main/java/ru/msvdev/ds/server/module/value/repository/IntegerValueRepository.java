package ru.msvdev.ds.server.module.value.repository;

import org.springframework.data.jdbc.repository.query.Query;


public interface IntegerValueRepository extends ValueRepository<Long> {

    @Override
    @Query("SELECT id FROM integer_values WHERE value = :value")
    Long findIdByValue(Long value);


    @Override
    @Query("SELECT value FROM integer_values WHERE id = :id")
    Long findValueById(long id);


    @Override
    @Query("""
            WITH value_id AS (
                INSERT INTO "values" (id) VALUES (DEFAULT)
                RETURNING id
            ), value AS (
                INSERT INTO integer_values (id, value) VALUES ((SELECT id FROM value_id), :value)
            )
            SELECT id FROM value_id
            """)
    Long insert(Long value);

}
