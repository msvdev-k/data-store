package ru.msvdev.ds.server.module.value.repository;

import org.springframework.data.jdbc.repository.query.Query;


public interface FileIdValueRepository extends ValueRepository<Long> {

    @Override
    @Query("SELECT id FROM file_id_values WHERE file_id = :value")
    Long findIdByValue(Long value);


    @Override
    @Query("SELECT file_id FROM file_id_values WHERE id = :id")
    Long findValueById(long id);


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
