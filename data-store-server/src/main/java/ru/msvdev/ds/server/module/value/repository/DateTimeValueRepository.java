package ru.msvdev.ds.server.module.value.repository;

import org.springframework.data.jdbc.repository.query.Query;

import java.time.OffsetDateTime;


public interface DateTimeValueRepository extends ValueRepository<OffsetDateTime> {

    @Override
    @Query("SELECT id FROM datetime_values WHERE value = :value")
    Long findIdByValue(OffsetDateTime value);


    @Override
    @Query("SELECT value FROM datetime_values WHERE id = :id")
    OffsetDateTime findValueById(long id);


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
