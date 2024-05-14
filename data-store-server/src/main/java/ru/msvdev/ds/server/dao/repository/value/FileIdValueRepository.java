package ru.msvdev.ds.server.dao.repository.value;

import org.springframework.data.jdbc.repository.query.Query;

import java.util.Optional;


public interface FileIdValueRepository extends ValueRepository<Long> {

    @Override
    @Query("SELECT id FROM file_id_values WHERE file_id = :value")
    Optional<Long> findIdByValue(Long value);


    @Override
    @Query("SELECT file_id FROM file_id_values WHERE id = :id")
    Optional<Long> findValueById(Long id);


    @Override
    @Query("""
            WITH value_id AS (
                INSERT INTO "values" (id) VALUES (DEFAULT)
                RETURNING id
            ), value AS (
                INSERT INTO file_id_values (id, file_id) VALUES ((SELECT id FROM value_id), :value)
            )
            SELECT id FROM value_id
            """)
    Long insert(Long value);

}
