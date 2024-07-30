package ru.msvdev.ds.server.module.value.repository;

import org.springframework.data.jdbc.repository.query.Query;


public interface DoubleValueRepository extends ValueRepository<Double> {

    @Override
    @Query("SELECT id FROM double_values WHERE value = :value")
    Long findIdByValue(Double value);


    @Override
    @Query("SELECT value FROM double_values WHERE id = :id")
    Double findValueById(long id);


    @Override
    @Query("""
            WITH value_id AS (
                INSERT INTO "values" (id) VALUES (DEFAULT)
                RETURNING id
            ), value AS (
                INSERT INTO double_values (id, value) VALUES ((SELECT id FROM value_id), :value)
            )
            SELECT id FROM value_id
            """)
    Long insert(Double value);

}
