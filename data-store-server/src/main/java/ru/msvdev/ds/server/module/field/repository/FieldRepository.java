package ru.msvdev.ds.server.module.field.repository;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.Repository;
import ru.msvdev.ds.server.module.field.entity.Field;
import ru.msvdev.ds.server.module.value.base.DataType;

import java.util.List;


public interface FieldRepository extends Repository<Field, Long> {

    @Query("SELECT EXISTS(SELECT id FROM fields WHERE id = :id AND catalog_id = :catalogId)")
    boolean existsById(long catalogId, long id);


    @Query("SELECT id FROM fields WHERE catalog_id = :catalogId AND name = :name")
    Long findIdByName(long catalogId, String name);


    @Query("""
            SELECT t.id, t.catalog_id, t."order", t.name, t.description, tp.type AS data_type, t.format
            FROM fields AS t
            INNER JOIN value_types AS tp ON tp.id = t.type_id
            WHERE t.id = :id AND t.catalog_id = :catalogId
            """)
    Field findById(long catalogId, long id);


    @Query("""
            SELECT t.id, t.catalog_id, t."order", t.name, t.description, tp.type AS data_type, t.format
            FROM fields AS t
            INNER JOIN value_types AS tp ON tp.id = t.type_id
            WHERE t.catalog_id = :catalogId
            """)
    List<Field> findAll(long catalogId);


    @Query("""
            WITH inserted_field_template AS (
                INSERT INTO fields (catalog_id, "order", name, description, type_id, format)
                VALUES (:catalogId, :order, :name, :description, :#{#type.id}, :format)
                RETURNING *
            )
            SELECT t.id, t.catalog_id, t."order", t.name, t.description, tp.type AS data_type, t.format
            FROM inserted_field_template AS t
            INNER JOIN value_types AS tp ON tp.id = t.type_id
            """)
    Field insert(long catalogId, int order, String name, DataType type, String format, String description);


    @Modifying
    @Query("UPDATE fields SET \"order\" = :newOrder WHERE id = :id AND catalog_id = :catalogId")
    boolean updateOrder(long catalogId, long id, int newOrder);

    @Modifying
    @Query("UPDATE fields SET name = :newName WHERE id = :id AND catalog_id = :catalogId")
    boolean updateName(long catalogId, long id, String newName);

    @Modifying
    @Query("UPDATE fields SET description = :newDescription WHERE id = :id AND catalog_id = :catalogId")
    boolean updateDescription(long catalogId, long id, String newDescription);

    @Modifying
    @Query("DELETE FROM fields WHERE id = :id AND catalog_id = :catalogId")
    boolean deleteById(long catalogId, long id);
}
