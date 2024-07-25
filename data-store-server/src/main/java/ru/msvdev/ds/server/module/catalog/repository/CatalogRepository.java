package ru.msvdev.ds.server.module.catalog.repository;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.Repository;
import ru.msvdev.ds.server.module.catalog.entity.Catalog;
import ru.msvdev.ds.server.security.Authority;

import java.util.List;
import java.util.UUID;


public interface CatalogRepository extends Repository<Catalog, Long> {

    @Query("""
            SELECT c.id, c.name, c.description, array_agg(a.authority) AS authorities
            FROM catalogs AS c
            INNER JOIN user_authorities AS a ON a.catalog_id = c.id
            WHERE c.id = :id AND a.user_uuid = :userUuid
            GROUP BY c.id
            """)
    Catalog findById(UUID userUuid, long id);


    @Query("""
            SELECT c.id, c.name, c.description, array_agg(a.authority) AS authorities
            FROM catalogs AS c
            INNER JOIN user_authorities AS a ON a.catalog_id = c.id
            WHERE a.user_uuid = :userUuid
            GROUP BY c.id
            """)
    List<Catalog> findAll(UUID userUuid);


    @Query("""
            WITH inserted_catalog AS (
                INSERT INTO catalogs (name, description)
                VALUES (:name, :description)
                RETURNING *
            ), inserted_authority AS (
                INSERT INTO user_authorities (catalog_id, user_uuid, authority)
                VALUES ((SELECT id FROM inserted_catalog), :userUuid, :masterAuthority)
                RETURNING *
            )
            SELECT c.id, c.name, c.description, a.authority AS authorities
            FROM inserted_catalog AS c, inserted_authority AS a
            """)
    Catalog insert(UUID userUuid, String name, String description, Authority masterAuthority);


    @Modifying
    @Query("UPDATE catalogs SET name = :newName WHERE id = :id")
    boolean updateName(long id, String newName);


    @Modifying
    @Query("UPDATE catalogs SET description = :newDescription WHERE id = :id")
    boolean updateDescription(long id, String newDescription);


    @Modifying
    @Query("DELETE FROM catalogs WHERE  id = :id")
    boolean deleteById(long id);

}
