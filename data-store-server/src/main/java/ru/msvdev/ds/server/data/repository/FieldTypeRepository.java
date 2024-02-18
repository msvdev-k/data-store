package ru.msvdev.ds.server.data.repository;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.Repository;
import ru.msvdev.ds.server.data.entity.FieldType;

import java.util.List;


public interface FieldTypeRepository extends Repository<FieldType, Integer> {

    @Query("SELECT id, type, description FROM field_types")
    List<FieldType> findAll();
}
