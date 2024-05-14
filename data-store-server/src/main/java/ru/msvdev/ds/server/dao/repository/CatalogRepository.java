package ru.msvdev.ds.server.dao.repository;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.Repository;
import ru.msvdev.ds.server.dao.entity.Catalog;
import ru.msvdev.ds.server.security.Authority;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


public interface CatalogRepository extends Repository<Catalog, Long> {

    @Query("SELECT * FROM catalogs WHERE id = :id")
    Optional<Catalog> findById(long id);

    @Query("""
            SELECT c.id, c.name, c.description, array_agg(a.authority) AS authorities
            FROM catalogs AS c
            INNER JOIN user_authorities as a ON a.catalog_id = c.id
            WHERE a.user_uuid = :userUuid
            GROUP BY c.id
            """)
    List<Catalog> findAll(UUID userUuid);


    @Query("""
            WITH inserted_catalog AS (
                INSERT INTO catalogs (name, description) VALUES (:name, :description)
                RETURNING id, name, description
            )
            SELECT * FROM inserted_catalog
            """)
    Catalog insert(String name, String description);

    @Modifying
    @Query("UPDATE catalogs SET name = :newName WHERE id = :id")
    boolean updateName(long id, String newName);

    @Modifying
    @Query("UPDATE catalogs SET description = :newDescription WHERE id = :id")
    boolean updateDescription(long id, String newDescription);

    @Modifying
    @Query("DELETE FROM catalogs WHERE  id = :id")
    boolean deleteById(long id);


    @Query("SELECT DISTINCT user_uuid FROM user_authorities WHERE catalog_id = :catalogId")
    List<UUID> findAllUsers(long catalogId);

    @Query("SELECT authority FROM user_authorities WHERE catalog_id = :catalogId AND user_uuid = :userUuid")
    List<Authority> findAllAuthorities(long catalogId, UUID userUuid);


    @Modifying
    @Query("INSERT INTO user_authorities (catalog_id, user_uuid, authority) VALUES (:catalogId, :userUuid, :authority)")
    boolean addAuthority(long catalogId, UUID userUuid, Authority authority);

    @Modifying
    @Query("DELETE FROM user_authorities WHERE catalog_id = :catalogId AND user_uuid = :userUuid AND authority = :authority")
    boolean removeAuthority(long catalogId, UUID userUuid, Authority authority);

    @Modifying
    @Query("DELETE FROM user_authorities WHERE catalog_id = :catalogId AND user_uuid = :userUuid")
    boolean removeAllAuthorities(long catalogId, UUID userUuid);


    @Deprecated // Use CardRepository::count
    @Query("SELECT count(*) FROM cards WHERE catalog_id = :catalogId")
    int cardsCount(long catalogId);
}
