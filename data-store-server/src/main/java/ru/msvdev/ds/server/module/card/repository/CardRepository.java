package ru.msvdev.ds.server.module.card.repository;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.Repository;
import ru.msvdev.ds.server.module.card.entity.Card;

import java.util.List;


public interface CardRepository extends Repository<Card, Long> {

    @Query("SELECT * FROM cards WHERE catalog_id = :catalogId")
    List<Card> getCards(long catalogId);


    @Query("SELECT count(*) FROM cards WHERE catalog_id = :catalogId")
    int count(long catalogId);


    @Query("SELECT EXISTS(SELECT id FROM cards WHERE id = :id AND catalog_id = :catalogId)")
    boolean existsById(long catalogId, long id);


    @Query("""
            WITH inserted_card AS (
                INSERT INTO cards (catalog_id) VALUES (:catalogId)
                RETURNING *
            )
            SELECT * FROM inserted_card
            """)
    Card insert(long catalogId);


    @Modifying
    @Query("DELETE FROM cards WHERE id = :id AND catalog_id = :catalogId")
    boolean deleteById(long catalogId, long id);
}
