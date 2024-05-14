package ru.msvdev.ds.server.dao.repository.value;

import org.springframework.data.jdbc.repository.query.Query;

import java.util.Optional;
import java.util.UUID;


public interface UuidValueRepository extends ValueRepository<UUID> {

    @Override
    @Query("SELECT id FROM uuid_values WHERE value = :value")
    Optional<Long> findIdByValue(UUID value);


    @Override
    @Query("SELECT value FROM uuid_values WHERE id = :id")
    Optional<UUID> findValueById(Long id);


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
