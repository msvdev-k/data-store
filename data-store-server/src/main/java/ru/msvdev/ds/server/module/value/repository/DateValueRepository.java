package ru.msvdev.ds.server.module.value.repository;

import org.springframework.data.jdbc.repository.query.Query;

import java.time.LocalDate;


public interface DateValueRepository extends ValueRepository<LocalDate> {

    @Override
    @Query("SELECT id FROM date_values WHERE value = :value")
    Long findIdByValue(LocalDate value);


    @Override
    @Query("SELECT value FROM date_values WHERE id = :id")
    LocalDate findValueById(long id);


    @Override
    @Query("""
            WITH value_id AS (
                INSERT INTO "values" (id) VALUES (DEFAULT)
                RETURNING id
            ), value AS (
                INSERT INTO date_values (id, value) VALUES ((SELECT id FROM value_id), :value)
            )
            SELECT id FROM value_id
            """)
    Long insert(LocalDate value);

}
