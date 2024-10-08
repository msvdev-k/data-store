package ru.msvdev.ds.server.module.value.repository;

import org.springframework.data.jdbc.repository.query.Query;

import java.util.UUID;


public interface UuidValueRepository extends ValueRepository<UUID> {

    @Override
    @Query("SELECT id FROM uuid_values WHERE value = :value")
    Long findIdByValue(UUID value);


    @Override
    @Query("SELECT value FROM uuid_values WHERE id = :id")
    UUID findValueById(long id);


    @Override
    @Query("""
            WITH value_id AS (
                INSERT INTO "values" (id) VALUES (DEFAULT)
                RETURNING id
            ), value AS (
                INSERT INTO uuid_values (id, value) VALUES ((SELECT id FROM value_id), :value)
            )
            SELECT id FROM value_id
            """)
    Long insert(UUID value);

}
