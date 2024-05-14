package ru.msvdev.ds.server.dao.repository.value;

import org.springframework.data.jdbc.repository.query.Query;

import java.math.BigDecimal;
import java.util.Optional;


public interface BigDecimalValueRepository extends ValueRepository<BigDecimal> {

    @Override
    @Query("SELECT id FROM bigdecimal_values WHERE value = :value")
    Optional<Long> findIdByValue(BigDecimal value);


    @Override
    @Query("SELECT value FROM bigdecimal_values WHERE id = :id")
    Optional<BigDecimal> findValueById(Long id);


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
