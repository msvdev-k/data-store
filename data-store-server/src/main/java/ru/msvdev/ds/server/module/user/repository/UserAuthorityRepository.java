package ru.msvdev.ds.server.module.user.repository;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.Repository;
import ru.msvdev.ds.server.module.user.entity.UserAuthority;
import ru.msvdev.ds.server.security.Authority;

import java.util.List;
import java.util.UUID;


public interface UserAuthorityRepository extends Repository<UserAuthority, Long> {

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
}
