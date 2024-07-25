package ru.msvdev.ds.server.module.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import ru.msvdev.ds.server.base.ApplicationTest;
import ru.msvdev.ds.server.module.user.repository.UserAuthorityRepository;
import ru.msvdev.ds.server.security.Authority;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;


@DataJdbcTest
@Sql(
        value = {"classpath:module/user/user-authority-repository-test.sql"},
        config = @SqlConfig(encoding = "UTF8")
)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserAuthorityRepositoryTest extends ApplicationTest {

    private final UserAuthorityRepository userAuthorityRepository;

    @Autowired
    public UserAuthorityRepositoryTest(UserAuthorityRepository userAuthorityRepository) {
        this.userAuthorityRepository = userAuthorityRepository;
    }


    @Test
    void findAllUsers() {
        // region Given
        int catalogId = 1;
        // endregion


        // region When
        List<UUID> users = userAuthorityRepository.findAllUsers(catalogId);
        // endregion


        // region Then
        assertEquals(3, users.size());

        assertTrue(users.contains(UUID.fromString("bfe5e92a-ba1f-4412-a5e9-2aba1fc41272")));
        assertTrue(users.contains(UUID.fromString("42b16c80-7987-462b-b16c-807987062be1")));
        assertTrue(users.contains(UUID.fromString("64f25d2f-953f-4605-b25d-2f953f260558")));
        //endregion
    }


    @Test
    void findAllAuthorities() {
        // region Given
        int catalogId = 2;
        UUID userUUID = UUID.fromString("bfe5e92a-ba1f-4412-a5e9-2aba1fc41272");
        Authority[] authorityTypes = Arrays.stream(Authority.values())
                .filter(authority -> authority != Authority.MASTER)
                .toArray(Authority[]::new);
        // endregion


        // region When
        List<Authority> authorities = userAuthorityRepository.findAllAuthorities(catalogId, userUUID);
        // endregion


        // region Then
        assertEquals(authorityTypes.length, authorities.size());
        for (Authority authority : authorityTypes) {
            assertTrue(authorities.contains(authority));
        }
        //endregion
    }


    @ParameterizedTest
    @EnumSource(Authority.class)
    void addAuthority(Authority authority) {
        // region Given
        int catalogId = 3;
        UUID userUUID = UUID.fromString("bfe5e92a-ba1f-4412-a5e9-2aba1fc41272");
        // endregion


        // region When
        boolean addFlag = userAuthorityRepository.addAuthority(catalogId, userUUID, authority);
        // endregion


        // region Then
        assertTrue(addFlag);

        List<Authority> authorities = userAuthorityRepository.findAllAuthorities(catalogId, userUUID);
        assertEquals(1, authorities.size());
        assertTrue(authorities.contains(authority));
        //endregion
    }


    @ParameterizedTest
    @EnumSource(value = Authority.class, names = {"MASTER"}, mode = EnumSource.Mode.EXCLUDE)
    void removeAuthority(Authority authority) {
        // region Given
        int catalogId = 2;
        UUID userUUID = UUID.fromString("bfe5e92a-ba1f-4412-a5e9-2aba1fc41272");
        Authority[] authorityTypes = Arrays.stream(Authority.values())
                .filter(a -> a != Authority.MASTER && a != authority)
                .toArray(Authority[]::new);
        // endregion


        // region When
        boolean removeFlag = userAuthorityRepository.removeAuthority(catalogId, userUUID, authority);
        // endregion


        // region Then
        assertTrue(removeFlag);

        List<Authority> authorities = userAuthorityRepository.findAllAuthorities(catalogId, userUUID);
        assertEquals(authorityTypes.length, authorities.size());
        for (Authority a : authorityTypes) {
            assertTrue(authorities.contains(a));
        }
        //endregion
    }


    @Test
    void removeAllAuthorities() {
        // region Given
        int catalogId = 2;
        UUID userUUID = UUID.fromString("bfe5e92a-ba1f-4412-a5e9-2aba1fc41272");
        // endregion


        // region When
        boolean removeFlag = userAuthorityRepository.removeAllAuthorities(catalogId, userUUID);
        // endregion


        // region Then
        assertTrue(removeFlag);

        List<Authority> authorities = userAuthorityRepository.findAllAuthorities(catalogId, userUUID);
        assertTrue(authorities.isEmpty());
        //endregion
    }
}