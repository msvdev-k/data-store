package ru.msvdev.ds.server.module.value.repository;

import org.springframework.data.jdbc.repository.query.Query;

import java.math.BigDecimal;


public interface BigDecimalValueRepository extends ValueRepository<BigDecimal> {

    @Override
    @Query("SELECT id FROM bigdecimal_values WHERE value = :value")
    Long findIdByValue(BigDecimal value);


    @Override
    @Query("SELECT value FROM bigdecimal_values WHERE id = :id")
    BigDecimal findValueById(long id);


    @Override
    @Query("""
            WITH value_id AS (
                INSERT INTO "values" (id) VALUES (DEFAULT)
                RETURNING id
            ), value AS (
                INSERT INTO bigdecimal_values (id, value) VALUES ((SELECT id FROM value_id), :value)
            )
            SELECT id FROM value_id
            """)
    Long insert(BigDecimal value);

}
