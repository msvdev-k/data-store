package ru.msvdev.ds.server.module.value.repository;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.Repository;
import ru.msvdev.ds.server.module.value.entity.ValueType;

import java.util.List;


public interface ValueTypeRepository extends Repository<ValueType, Integer> {

    @Query("SELECT id, type, description FROM value_types")
    List<ValueType> findAll();
}
