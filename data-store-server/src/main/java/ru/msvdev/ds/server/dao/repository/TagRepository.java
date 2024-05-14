package ru.msvdev.ds.server.dao.repository;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.Repository;
import ru.msvdev.ds.server.dao.entity.Tag;

import java.util.List;


public interface TagRepository extends Repository<Tag, Tag> {

    @Query("SELECT EXISTS(SELECT * FROM tags WHERE card_id = :cardId AND field_id = :fieldId AND value_id = :valueId)")
    boolean isExists(long cardId, long fieldId, long valueId);


    @Query("""
            SELECT t.card_id, t.field_id, ft.type AS value_type, t.value_id
            FROM       tags        AS t
            INNER JOIN cards       AS c  ON c.id  = t.card_id
            INNER JOIN fields      AS f  ON f.id  = t.field_id
            INNER JOIN field_types AS ft ON ft.id = f.type_id
            WHERE c.id = :cardId AND c.catalog_id = :catalogId
            """)
    List<Tag> findAll(long catalogId, long cardId);


    @Modifying
    @Query("INSERT INTO tags (card_id, field_id, value_id) VALUES (:cardId, :fieldId, :valueId)")
    boolean insert(long cardId, long fieldId, long valueId);


    @Modifying
    @Query("DELETE FROM tags WHERE card_id in (SELECT id FROM cards WHERE id = :cardId AND catalog_id = :catalogId)")
    boolean delete(long catalogId, long cardId);


    @Modifying
    @Query("DELETE FROM tags WHERE field_id = :fieldId AND card_id in (SELECT id FROM cards WHERE id = :cardId AND catalog_id = :catalogId)")
    boolean delete(long catalogId, long cardId, long fieldId);
}
