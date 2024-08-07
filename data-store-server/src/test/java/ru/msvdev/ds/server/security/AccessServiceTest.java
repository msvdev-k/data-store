package ru.msvdev.ds.server.security;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import org.springframework.http.HttpMethod;
import ru.msvdev.ds.server.config.SecurityConfig;

import java.util.*;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.http.HttpMethod.*;
import static ru.msvdev.ds.server.security.Permission.FORBIDDEN;
import static ru.msvdev.ds.server.security.Permission.OK;


public class AccessServiceTest {

    private static AccessService accessService;

    private final static long CATALOG_ID = 1;
    private final static String ALL_AUTHORITIES_WITHOUT_MASTER = "ALL_AUTHORITIES_WITHOUT_MASTER";

    private final static Map<String, UUID> users = new HashMap<>();
    private final static Map<String, List<Authority>> authorities = new HashMap<>();


    @BeforeAll
    static void init() {
        AuthorityService mockAuthorityService = Mockito.mock(AuthorityService.class);

        users.put(ALL_AUTHORITIES_WITHOUT_MASTER, UUID.randomUUID());
        authorities.put(ALL_AUTHORITIES_WITHOUT_MASTER, new ArrayList<>());

        for (Authority authority : Authority.values()) {
            String authority_name = authority.name();

            users.put(authority_name, UUID.randomUUID());
            authorities.put(authority_name, List.of(authority));

            Mockito
                    .when(mockAuthorityService.loadAuthorities(users.get(authority_name), CATALOG_ID))
                    .thenReturn(authorities.get(authority_name));

            if (authority != Authority.MASTER) {
                authorities.get(ALL_AUTHORITIES_WITHOUT_MASTER).add(authority);
            }
        }

        Mockito
                .when(mockAuthorityService.loadAuthorities(users.get(ALL_AUTHORITIES_WITHOUT_MASTER), CATALOG_ID))
                .thenReturn(authorities.get(ALL_AUTHORITIES_WITHOUT_MASTER));

        accessService = new SecurityConfig().catalogAccessService(mockAuthorityService);
    }


    @ParameterizedTest
    @MethodSource({
            "getPermissionsMethodSource_MASTER",
            "getPermissionsMethodSource_GRANT_AUTHORITY",
            "getPermissionsMethodSource_READING",
            "getPermissionsMethodSource_WRITING",
            "getPermissionsMethodSource_DELETING",
            "getPermissionsMethodSource_FIELD_TEMPLATE_WRITING",
            "getPermissionsMethodSource_FIELD_TEMPLATE_DELETING",
            "getPermissionsMethodSource_FILE_UPLOAD",
            "getPermissionsMethodSource_FILE_DOWNLOAD",
            "getPermissionsMethodSource_FILE_SYSTEM_READ",
            "getPermissionsMethodSource_FILE_SYSTEM_WRITE",
            "getPermissionsMethodSource_FILE_SYSTEM_DELETE",
            "getPermissionsMethodSource_ALL_AUTHORITIES_WITHOUT_MASTER"
    })
    void getPermission(UUID userUuid, List<Authority> authorities, String path, HttpMethod httpMethod, Permission expectedPermission) {
        // region Given
        HttpRequest httpRequest = new HttpRequest(
                userUuid,
                path.split("/"),
                httpMethod,
                new HashSet<>(authorities)
        );
        // endregion


        // region When
        Permission permission = accessService.getPermission(httpRequest);
        // endregion


        // region Then
        assertEquals(expectedPermission, permission);
        // endregion
    }


    private static Stream<Arguments> getPermissionsMethodSource_MASTER() {
        UUID userUuid = users.get(Authority.MASTER.name());
        List<Authority> authorities = AccessServiceTest.authorities.get(Authority.MASTER.name());

        return Stream.of(
                Arguments.of(userUuid, authorities, "/catalog", GET, OK),
                Arguments.of(userUuid, authorities, "/catalog", POST, OK),
                Arguments.of(userUuid, authorities, "/catalog/1", PUT, OK),
                Arguments.of(userUuid, authorities, "/catalog/1", DELETE, OK),
                Arguments.of(userUuid, authorities, "/catalog/1/field", GET, OK),
                Arguments.of(userUuid, authorities, "/catalog/1/field", POST, OK),
                Arguments.of(userUuid, authorities, "/catalog/1/field/*", PUT, OK),
                Arguments.of(userUuid, authorities, "/catalog/1/field/*", DELETE, OK),
                Arguments.of(userUuid, authorities, "/catalog/1/card", GET, OK),
                Arguments.of(userUuid, authorities, "/catalog/1/card", POST, OK),
                Arguments.of(userUuid, authorities, "/catalog/1/card/*", GET, OK),
                Arguments.of(userUuid, authorities, "/catalog/1/card/*", DELETE, OK),
                Arguments.of(userUuid, authorities, "/catalog/1/card/*/tag", PUT, OK),
                Arguments.of(userUuid, authorities, "/catalog/1/card/*/tag", DELETE, OK),
                Arguments.of(userUuid, authorities, "/catalog/1/card/*/tag/*", DELETE, OK),
                Arguments.of(userUuid, authorities, "/catalog/1/user", GET, OK),
                Arguments.of(userUuid, authorities, "/catalog/1/user", PUT, OK),
                Arguments.of(userUuid, authorities, "/catalog/1/user", DELETE, OK),
                Arguments.of(userUuid, authorities, "/catalog/1/upload", POST, OK),
                Arguments.of(userUuid, authorities, "/catalog/1/upload", PUT, OK),
                Arguments.of(userUuid, authorities, "/catalog/1/fs/*", GET, OK),
                Arguments.of(userUuid, authorities, "/catalog/1/fs/*", POST, OK),
                Arguments.of(userUuid, authorities, "/catalog/1/fs/*", PUT, OK),
                Arguments.of(userUuid, authorities, "/catalog/1/fs/*", DELETE, OK),
                Arguments.of(userUuid, authorities, "/catalog/1/fs/*/download", GET, OK)
        );
    }

    private static Stream<Arguments> getPermissionsMethodSource_GRANT_AUTHORITY() {
        UUID userUuid = users.get(Authority.GRANT_AUTHORITY.name());
        List<Authority> authorities = AccessServiceTest.authorities.get(Authority.GRANT_AUTHORITY.name());

        return Stream.of(
                Arguments.of(userUuid, authorities, "/catalog", GET, OK),
                Arguments.of(userUuid, authorities, "/catalog", POST, OK),
                Arguments.of(userUuid, authorities, "/catalog/1", PUT, FORBIDDEN),
                Arguments.of(userUuid, authorities, "/catalog/1", DELETE, FORBIDDEN),
                Arguments.of(userUuid, authorities, "/catalog/1/field", GET, FORBIDDEN),
                Arguments.of(userUuid, authorities, "/catalog/1/field", POST, FORBIDDEN),
                Arguments.of(userUuid, authorities, "/catalog/1/field/*", PUT, FORBIDDEN),
                Arguments.of(userUuid, authorities, "/catalog/1/field/*", DELETE, FORBIDDEN),
                Arguments.of(userUuid, authorities, "/catalog/1/card", GET, FORBIDDEN),
                Arguments.of(userUuid, authorities, "/catalog/1/card", POST, FORBIDDEN),
                Arguments.of(userUuid, authorities, "/catalog/1/card/*", GET, FORBIDDEN),
                Arguments.of(userUuid, authorities, "/catalog/1/card/*", DELETE, FORBIDDEN),
                Arguments.of(userUuid, authorities, "/catalog/1/card/*/tag", PUT, FORBIDDEN),
                Arguments.of(userUuid, authorities, "/catalog/1/card/*/tag", DELETE, FORBIDDEN),
                Arguments.of(userUuid, authorities, "/catalog/1/card/*/tag/*", DELETE, FORBIDDEN),
                Arguments.of(userUuid, authorities, "/catalog/1/user", GET, OK),
                Arguments.of(userUuid, authorities, "/catalog/1/user", PUT, OK),
                Arguments.of(userUuid, authorities, "/catalog/1/user", DELETE, OK),
                Arguments.of(userUuid, authorities, "/catalog/1/upload", POST, FORBIDDEN),
                Arguments.of(userUuid, authorities, "/catalog/1/upload", PUT, FORBIDDEN),
                Arguments.of(userUuid, authorities, "/catalog/1/fs/*", GET, FORBIDDEN),
                Arguments.of(userUuid, authorities, "/catalog/1/fs/*", POST, FORBIDDEN),
                Arguments.of(userUuid, authorities, "/catalog/1/fs/*", PUT, FORBIDDEN),
                Arguments.of(userUuid, authorities, "/catalog/1/fs/*", DELETE, FORBIDDEN),
                Arguments.of(userUuid, authorities, "/catalog/1/fs/*/download", GET, FORBIDDEN)
        );
    }

    private static Stream<Arguments> getPermissionsMethodSource_READING() {
        UUID userUuid = users.get(Authority.READING.name());
        List<Authority> authorities = AccessServiceTest.authorities.get(Authority.READING.name());

        return Stream.of(
                Arguments.of(userUuid, authorities, "/catalog", GET, OK),
                Arguments.of(userUuid, authorities, "/catalog", POST, OK),
                Arguments.of(userUuid, authorities, "/catalog/1", PUT, FORBIDDEN),
                Arguments.of(userUuid, authorities, "/catalog/1", DELETE, FORBIDDEN),
                Arguments.of(userUuid, authorities, "/catalog/1/field", GET, OK),
                Arguments.of(userUuid, authorities, "/catalog/1/field", POST, FORBIDDEN),
                Arguments.of(userUuid, authorities, "/catalog/1/field/*", PUT, FORBIDDEN),
                Arguments.of(userUuid, authorities, "/catalog/1/field/*", DELETE, FORBIDDEN),
                Arguments.of(userUuid, authorities, "/catalog/1/card", GET, OK),
                Arguments.of(userUuid, authorities, "/catalog/1/card", POST, FORBIDDEN),
                Arguments.of(userUuid, authorities, "/catalog/1/card/*", GET, OK),
                Arguments.of(userUuid, authorities, "/catalog/1/card/*", DELETE, FORBIDDEN),
                Arguments.of(userUuid, authorities, "/catalog/1/card/*/tag", PUT, FORBIDDEN),
                Arguments.of(userUuid, authorities, "/catalog/1/card/*/tag", DELETE, FORBIDDEN),
                Arguments.of(userUuid, authorities, "/catalog/1/card/*/tag/*", DELETE, FORBIDDEN),
                Arguments.of(userUuid, authorities, "/catalog/1/user", GET, FORBIDDEN),
                Arguments.of(userUuid, authorities, "/catalog/1/user", PUT, FORBIDDEN),
                Arguments.of(userUuid, authorities, "/catalog/1/user", DELETE, FORBIDDEN),
                Arguments.of(userUuid, authorities, "/catalog/1/upload", POST, FORBIDDEN),
                Arguments.of(userUuid, authorities, "/catalog/1/upload", PUT, FORBIDDEN),
                Arguments.of(userUuid, authorities, "/catalog/1/fs/*", GET, FORBIDDEN),
                Arguments.of(userUuid, authorities, "/catalog/1/fs/*", POST, FORBIDDEN),
                Arguments.of(userUuid, authorities, "/catalog/1/fs/*", PUT, FORBIDDEN),
                Arguments.of(userUuid, authorities, "/catalog/1/fs/*", DELETE, FORBIDDEN),
                Arguments.of(userUuid, authorities, "/catalog/1/fs/*/download", GET, FORBIDDEN)
        );
    }

    private static Stream<Arguments> getPermissionsMethodSource_WRITING() {
        UUID userUuid = users.get(Authority.WRITING.name());
        List<Authority> authorities = AccessServiceTest.authorities.get(Authority.WRITING.name());

        return Stream.of(
                Arguments.of(userUuid, authorities, "/catalog", GET, OK),
                Arguments.of(userUuid, authorities, "/catalog", POST, OK),
                Arguments.of(userUuid, authorities, "/catalog/1", PUT, FORBIDDEN),
                Arguments.of(userUuid, authorities, "/catalog/1", DELETE, FORBIDDEN),
                Arguments.of(userUuid, authorities, "/catalog/1/field", GET, FORBIDDEN),
                Arguments.of(userUuid, authorities, "/catalog/1/field", POST, FORBIDDEN),
                Arguments.of(userUuid, authorities, "/catalog/1/field/*", PUT, FORBIDDEN),
                Arguments.of(userUuid, authorities, "/catalog/1/field/*", DELETE, FORBIDDEN),
                Arguments.of(userUuid, authorities, "/catalog/1/card", GET, FORBIDDEN),
                Arguments.of(userUuid, authorities, "/catalog/1/card", POST, OK),
                Arguments.of(userUuid, authorities, "/catalog/1/card/*", GET, FORBIDDEN),
                Arguments.of(userUuid, authorities, "/catalog/1/card/*", DELETE, FORBIDDEN),
                Arguments.of(userUuid, authorities, "/catalog/1/card/*/tag", PUT, OK),
                Arguments.of(userUuid, authorities, "/catalog/1/card/*/tag", DELETE, FORBIDDEN),
                Arguments.of(userUuid, authorities, "/catalog/1/card/*/tag/*", DELETE, OK),
                Arguments.of(userUuid, authorities, "/catalog/1/user", GET, FORBIDDEN),
                Arguments.of(userUuid, authorities, "/catalog/1/user", PUT, FORBIDDEN),
                Arguments.of(userUuid, authorities, "/catalog/1/user", DELETE, FORBIDDEN),
                Arguments.of(userUuid, authorities, "/catalog/1/upload", POST, FORBIDDEN),
                Arguments.of(userUuid, authorities, "/catalog/1/upload", PUT, FORBIDDEN),
                Arguments.of(userUuid, authorities, "/catalog/1/fs/*", GET, FORBIDDEN),
                Arguments.of(userUuid, authorities, "/catalog/1/fs/*", POST, FORBIDDEN),
                Arguments.of(userUuid, authorities, "/catalog/1/fs/*", PUT, FORBIDDEN),
                Arguments.of(userUuid, authorities, "/catalog/1/fs/*", DELETE, FORBIDDEN),
                Arguments.of(userUuid, authorities, "/catalog/1/fs/*/download", GET, FORBIDDEN)
        );
    }

    private static Stream<Arguments> getPermissionsMethodSource_DELETING() {
        UUID userUuid = users.get(Authority.DELETING.name());
        List<Authority> authorities = AccessServiceTest.authorities.get(Authority.DELETING.name());

        return Stream.of(
                Arguments.of(userUuid, authorities, "/catalog", GET, OK),
                Arguments.of(userUuid, authorities, "/catalog", POST, OK),
                Arguments.of(userUuid, authorities, "/catalog/1", PUT, FORBIDDEN),
                Arguments.of(userUuid, authorities, "/catalog/1", DELETE, FORBIDDEN),
                Arguments.of(userUuid, authorities, "/catalog/1/field", GET, FORBIDDEN),
                Arguments.of(userUuid, authorities, "/catalog/1/field", POST, FORBIDDEN),
                Arguments.of(userUuid, authorities, "/catalog/1/field/*", PUT, FORBIDDEN),
                Arguments.of(userUuid, authorities, "/catalog/1/field/*", DELETE, FORBIDDEN),
                Arguments.of(userUuid, authorities, "/catalog/1/card", GET, FORBIDDEN),
                Arguments.of(userUuid, authorities, "/catalog/1/card", POST, FORBIDDEN),
                Arguments.of(userUuid, authorities, "/catalog/1/card/*", GET, FORBIDDEN),
                Arguments.of(userUuid, authorities, "/catalog/1/card/*", DELETE, OK),
                Arguments.of(userUuid, authorities, "/catalog/1/card/*/tag", PUT, FORBIDDEN),
                Arguments.of(userUuid, authorities, "/catalog/1/card/*/tag", DELETE, OK),
                Arguments.of(userUuid, authorities, "/catalog/1/card/*/tag/*", DELETE, FORBIDDEN),
                Arguments.of(userUuid, authorities, "/catalog/1/user", GET, FORBIDDEN),
                Arguments.of(userUuid, authorities, "/catalog/1/user", PUT, FORBIDDEN),
                Arguments.of(userUuid, authorities, "/catalog/1/user", DELETE, FORBIDDEN),
                Arguments.of(userUuid, authorities, "/catalog/1/upload", POST, FORBIDDEN),
                Arguments.of(userUuid, authorities, "/catalog/1/upload", PUT, FORBIDDEN),
                Arguments.of(userUuid, authorities, "/catalog/1/fs/*", GET, FORBIDDEN),
                Arguments.of(userUuid, authorities, "/catalog/1/fs/*", POST, FORBIDDEN),
                Arguments.of(userUuid, authorities, "/catalog/1/fs/*", PUT, FORBIDDEN),
                Arguments.of(userUuid, authorities, "/catalog/1/fs/*", DELETE, FORBIDDEN),
                Arguments.of(userUuid, authorities, "/catalog/1/fs/*/download", GET, FORBIDDEN)
        );
    }

    private static Stream<Arguments> getPermissionsMethodSource_FIELD_TEMPLATE_WRITING() {
        UUID userUuid = users.get(Authority.FIELD_TEMPLATE_WRITING.name());
        List<Authority> authorities = AccessServiceTest.authorities.get(Authority.FIELD_TEMPLATE_WRITING.name());

        return Stream.of(
                Arguments.of(userUuid, authorities, "/catalog", GET, OK),
                Arguments.of(userUuid, authorities, "/catalog", POST, OK),
                Arguments.of(userUuid, authorities, "/catalog/1", PUT, FORBIDDEN),
                Arguments.of(userUuid, authorities, "/catalog/1", DELETE, FORBIDDEN),
                Arguments.of(userUuid, authorities, "/catalog/1/field", GET, OK),
                Arguments.of(userUuid, authorities, "/catalog/1/field", POST, OK),
                Arguments.of(userUuid, authorities, "/catalog/1/field/*", PUT, OK),
                Arguments.of(userUuid, authorities, "/catalog/1/field/*", DELETE, FORBIDDEN),
                Arguments.of(userUuid, authorities, "/catalog/1/card", GET, FORBIDDEN),
                Arguments.of(userUuid, authorities, "/catalog/1/card", POST, FORBIDDEN),
                Arguments.of(userUuid, authorities, "/catalog/1/card/*", GET, FORBIDDEN),
                Arguments.of(userUuid, authorities, "/catalog/1/card/*", DELETE, FORBIDDEN),
                Arguments.of(userUuid, authorities, "/catalog/1/card/*/tag", PUT, FORBIDDEN),
                Arguments.of(userUuid, authorities, "/catalog/1/card/*/tag", DELETE, FORBIDDEN),
                Arguments.of(userUuid, authorities, "/catalog/1/card/*/tag/*", DELETE, FORBIDDEN),
                Arguments.of(userUuid, authorities, "/catalog/1/user", GET, FORBIDDEN),
                Arguments.of(userUuid, authorities, "/catalog/1/user", PUT, FORBIDDEN),
                Arguments.of(userUuid, authorities, "/catalog/1/user", DELETE, FORBIDDEN),
                Arguments.of(userUuid, authorities, "/catalog/1/upload", POST, FORBIDDEN),
                Arguments.of(userUuid, authorities, "/catalog/1/upload", PUT, FORBIDDEN),
                Arguments.of(userUuid, authorities, "/catalog/1/fs/*", GET, FORBIDDEN),
                Arguments.of(userUuid, authorities, "/catalog/1/fs/*", POST, FORBIDDEN),
                Arguments.of(userUuid, authorities, "/catalog/1/fs/*", PUT, FORBIDDEN),
                Arguments.of(userUuid, authorities, "/catalog/1/fs/*", DELETE, FORBIDDEN),
                Arguments.of(userUuid, authorities, "/catalog/1/fs/*/download", GET, FORBIDDEN)
        );
    }

    private static Stream<Arguments> getPermissionsMethodSource_FIELD_TEMPLATE_DELETING() {
        UUID userUuid = users.get(Authority.FIELD_TEMPLATE_DELETING.name());
        List<Authority> authorities = AccessServiceTest.authorities.get(Authority.FIELD_TEMPLATE_DELETING.name());

        return Stream.of(
                Arguments.of(userUuid, authorities, "/catalog", GET, OK),
                Arguments.of(userUuid, authorities, "/catalog", POST, OK),
                Arguments.of(userUuid, authorities, "/catalog/1", PUT, FORBIDDEN),
                Arguments.of(userUuid, authorities, "/catalog/1", DELETE, FORBIDDEN),
                Arguments.of(userUuid, authorities, "/catalog/1/field", GET, FORBIDDEN),
                Arguments.of(userUuid, authorities, "/catalog/1/field", POST, FORBIDDEN),
                Arguments.of(userUuid, authorities, "/catalog/1/field/*", PUT, FORBIDDEN),
                Arguments.of(userUuid, authorities, "/catalog/1/field/*", DELETE, OK),
                Arguments.of(userUuid, authorities, "/catalog/1/card", GET, FORBIDDEN),
                Arguments.of(userUuid, authorities, "/catalog/1/card", POST, FORBIDDEN),
                Arguments.of(userUuid, authorities, "/catalog/1/card/*", GET, FORBIDDEN),
                Arguments.of(userUuid, authorities, "/catalog/1/card/*", DELETE, FORBIDDEN),
                Arguments.of(userUuid, authorities, "/catalog/1/card/*/tag", PUT, FORBIDDEN),
                Arguments.of(userUuid, authorities, "/catalog/1/card/*/tag", DELETE, FORBIDDEN),
                Arguments.of(userUuid, authorities, "/catalog/1/card/*/tag/*", DELETE, FORBIDDEN),
                Arguments.of(userUuid, authorities, "/catalog/1/user", GET, FORBIDDEN),
                Arguments.of(userUuid, authorities, "/catalog/1/user", PUT, FORBIDDEN),
                Arguments.of(userUuid, authorities, "/catalog/1/user", DELETE, FORBIDDEN),
                Arguments.of(userUuid, authorities, "/catalog/1/upload", POST, FORBIDDEN),
                Arguments.of(userUuid, authorities, "/catalog/1/upload", PUT, FORBIDDEN),
                Arguments.of(userUuid, authorities, "/catalog/1/fs/*", GET, FORBIDDEN),
                Arguments.of(userUuid, authorities, "/catalog/1/fs/*", POST, FORBIDDEN),
                Arguments.of(userUuid, authorities, "/catalog/1/fs/*", PUT, FORBIDDEN),
                Arguments.of(userUuid, authorities, "/catalog/1/fs/*", DELETE, FORBIDDEN),
                Arguments.of(userUuid, authorities, "/catalog/1/fs/*/download", GET, FORBIDDEN)
        );
    }

    private static Stream<Arguments> getPermissionsMethodSource_FILE_UPLOAD() {
        UUID userUuid = users.get(Authority.FILE_UPLOAD.name());
        List<Authority> authorities = AccessServiceTest.authorities.get(Authority.FILE_UPLOAD.name());

        return Stream.of(
                Arguments.of(userUuid, authorities, "/catalog", GET, OK),
                Arguments.of(userUuid, authorities, "/catalog", POST, OK),
                Arguments.of(userUuid, authorities, "/catalog/1", PUT, FORBIDDEN),
                Arguments.of(userUuid, authorities, "/catalog/1", DELETE, FORBIDDEN),
                Arguments.of(userUuid, authorities, "/catalog/1/field", GET, FORBIDDEN),
                Arguments.of(userUuid, authorities, "/catalog/1/field", POST, FORBIDDEN),
                Arguments.of(userUuid, authorities, "/catalog/1/field/*", PUT, FORBIDDEN),
                Arguments.of(userUuid, authorities, "/catalog/1/field/*", DELETE, FORBIDDEN),
                Arguments.of(userUuid, authorities, "/catalog/1/card", GET, FORBIDDEN),
                Arguments.of(userUuid, authorities, "/catalog/1/card", POST, FORBIDDEN),
                Arguments.of(userUuid, authorities, "/catalog/1/card/*", GET, FORBIDDEN),
                Arguments.of(userUuid, authorities, "/catalog/1/card/*", DELETE, FORBIDDEN),
                Arguments.of(userUuid, authorities, "/catalog/1/card/*/tag", PUT, FORBIDDEN),
                Arguments.of(userUuid, authorities, "/catalog/1/card/*/tag", DELETE, FORBIDDEN),
                Arguments.of(userUuid, authorities, "/catalog/1/card/*/tag/*", DELETE, FORBIDDEN),
                Arguments.of(userUuid, authorities, "/catalog/1/user", GET, FORBIDDEN),
                Arguments.of(userUuid, authorities, "/catalog/1/user", PUT, FORBIDDEN),
                Arguments.of(userUuid, authorities, "/catalog/1/user", DELETE, FORBIDDEN),
                Arguments.of(userUuid, authorities, "/catalog/1/upload", POST, OK),
                Arguments.of(userUuid, authorities, "/catalog/1/upload", PUT, OK),
                Arguments.of(userUuid, authorities, "/catalog/1/fs/*", GET, FORBIDDEN),
                Arguments.of(userUuid, authorities, "/catalog/1/fs/*", POST, FORBIDDEN),
                Arguments.of(userUuid, authorities, "/catalog/1/fs/*", PUT, FORBIDDEN),
                Arguments.of(userUuid, authorities, "/catalog/1/fs/*", DELETE, FORBIDDEN),
                Arguments.of(userUuid, authorities, "/catalog/1/fs/*/download", GET, FORBIDDEN)
        );
    }

    private static Stream<Arguments> getPermissionsMethodSource_FILE_DOWNLOAD() {
        UUID userUuid = users.get(Authority.FILE_DOWNLOAD.name());
        List<Authority> authorities = AccessServiceTest.authorities.get(Authority.FILE_DOWNLOAD.name());

        return Stream.of(
                Arguments.of(userUuid, authorities, "/catalog", GET, OK),
                Arguments.of(userUuid, authorities, "/catalog", POST, OK),
                Arguments.of(userUuid, authorities, "/catalog/1", PUT, FORBIDDEN),
                Arguments.of(userUuid, authorities, "/catalog/1", DELETE, FORBIDDEN),
                Arguments.of(userUuid, authorities, "/catalog/1/field", GET, FORBIDDEN),
                Arguments.of(userUuid, authorities, "/catalog/1/field", POST, FORBIDDEN),
                Arguments.of(userUuid, authorities, "/catalog/1/field/*", PUT, FORBIDDEN),
                Arguments.of(userUuid, authorities, "/catalog/1/field/*", DELETE, FORBIDDEN),
                Arguments.of(userUuid, authorities, "/catalog/1/card", GET, FORBIDDEN),
                Arguments.of(userUuid, authorities, "/catalog/1/card", POST, FORBIDDEN),
                Arguments.of(userUuid, authorities, "/catalog/1/card/*", GET, FORBIDDEN),
                Arguments.of(userUuid, authorities, "/catalog/1/card/*", DELETE, FORBIDDEN),
                Arguments.of(userUuid, authorities, "/catalog/1/card/*/tag", PUT, FORBIDDEN),
                Arguments.of(userUuid, authorities, "/catalog/1/card/*/tag", DELETE, FORBIDDEN),
                Arguments.of(userUuid, authorities, "/catalog/1/card/*/tag/*", DELETE, FORBIDDEN),
                Arguments.of(userUuid, authorities, "/catalog/1/user", GET, FORBIDDEN),
                Arguments.of(userUuid, authorities, "/catalog/1/user", PUT, FORBIDDEN),
                Arguments.of(userUuid, authorities, "/catalog/1/user", DELETE, FORBIDDEN),
                Arguments.of(userUuid, authorities, "/catalog/1/upload", POST, FORBIDDEN),
                Arguments.of(userUuid, authorities, "/catalog/1/upload", PUT, FORBIDDEN),
                Arguments.of(userUuid, authorities, "/catalog/1/fs/*", GET, FORBIDDEN),
                Arguments.of(userUuid, authorities, "/catalog/1/fs/*", POST, FORBIDDEN),
                Arguments.of(userUuid, authorities, "/catalog/1/fs/*", PUT, FORBIDDEN),
                Arguments.of(userUuid, authorities, "/catalog/1/fs/*", DELETE, FORBIDDEN),
                Arguments.of(userUuid, authorities, "/catalog/1/fs/*/download", GET, OK)
        );
    }

    private static Stream<Arguments> getPermissionsMethodSource_FILE_SYSTEM_READ() {
        UUID userUuid = users.get(Authority.FILE_SYSTEM_READ.name());
        List<Authority> authorities = AccessServiceTest.authorities.get(Authority.FILE_SYSTEM_READ.name());

        return Stream.of(
                Arguments.of(userUuid, authorities, "/catalog", GET, OK),
                Arguments.of(userUuid, authorities, "/catalog", POST, OK),
                Arguments.of(userUuid, authorities, "/catalog/1", PUT, FORBIDDEN),
                Arguments.of(userUuid, authorities, "/catalog/1", DELETE, FORBIDDEN),
                Arguments.of(userUuid, authorities, "/catalog/1/field", GET, FORBIDDEN),
                Arguments.of(userUuid, authorities, "/catalog/1/field", POST, FORBIDDEN),
                Arguments.of(userUuid, authorities, "/catalog/1/field/*", PUT, FORBIDDEN),
                Arguments.of(userUuid, authorities, "/catalog/1/field/*", DELETE, FORBIDDEN),
                Arguments.of(userUuid, authorities, "/catalog/1/card", GET, FORBIDDEN),
                Arguments.of(userUuid, authorities, "/catalog/1/card", POST, FORBIDDEN),
                Arguments.of(userUuid, authorities, "/catalog/1/card/*", GET, FORBIDDEN),
                Arguments.of(userUuid, authorities, "/catalog/1/card/*", DELETE, FORBIDDEN),
                Arguments.of(userUuid, authorities, "/catalog/1/card/*/tag", PUT, FORBIDDEN),
                Arguments.of(userUuid, authorities, "/catalog/1/card/*/tag", DELETE, FORBIDDEN),
                Arguments.of(userUuid, authorities, "/catalog/1/card/*/tag/*", DELETE, FORBIDDEN),
                Arguments.of(userUuid, authorities, "/catalog/1/user", GET, FORBIDDEN),
                Arguments.of(userUuid, authorities, "/catalog/1/user", PUT, FORBIDDEN),
                Arguments.of(userUuid, authorities, "/catalog/1/user", DELETE, FORBIDDEN),
                Arguments.of(userUuid, authorities, "/catalog/1/upload", POST, FORBIDDEN),
                Arguments.of(userUuid, authorities, "/catalog/1/upload", PUT, FORBIDDEN),
                Arguments.of(userUuid, authorities, "/catalog/1/fs/*", GET, OK),
                Arguments.of(userUuid, authorities, "/catalog/1/fs/*", POST, FORBIDDEN),
                Arguments.of(userUuid, authorities, "/catalog/1/fs/*", PUT, FORBIDDEN),
                Arguments.of(userUuid, authorities, "/catalog/1/fs/*", DELETE, FORBIDDEN),
                Arguments.of(userUuid, authorities, "/catalog/1/fs/*/download", GET, FORBIDDEN)
        );
    }

    private static Stream<Arguments> getPermissionsMethodSource_FILE_SYSTEM_WRITE() {
        UUID userUuid = users.get(Authority.FILE_SYSTEM_WRITE.name());
        List<Authority> authorities = AccessServiceTest.authorities.get(Authority.FILE_SYSTEM_WRITE.name());

        return Stream.of(
                Arguments.of(userUuid, authorities, "/catalog", GET, OK),
                Arguments.of(userUuid, authorities, "/catalog", POST, OK),
                Arguments.of(userUuid, authorities, "/catalog/1", PUT, FORBIDDEN),
                Arguments.of(userUuid, authorities, "/catalog/1", DELETE, FORBIDDEN),
                Arguments.of(userUuid, authorities, "/catalog/1/field", GET, FORBIDDEN),
                Arguments.of(userUuid, authorities, "/catalog/1/field", POST, FORBIDDEN),
                Arguments.of(userUuid, authorities, "/catalog/1/field/*", PUT, FORBIDDEN),
                Arguments.of(userUuid, authorities, "/catalog/1/field/*", DELETE, FORBIDDEN),
                Arguments.of(userUuid, authorities, "/catalog/1/card", GET, FORBIDDEN),
                Arguments.of(userUuid, authorities, "/catalog/1/card", POST, FORBIDDEN),
                Arguments.of(userUuid, authorities, "/catalog/1/card/*", GET, FORBIDDEN),
                Arguments.of(userUuid, authorities, "/catalog/1/card/*", DELETE, FORBIDDEN),
                Arguments.of(userUuid, authorities, "/catalog/1/card/*/tag", PUT, FORBIDDEN),
                Arguments.of(userUuid, authorities, "/catalog/1/card/*/tag", DELETE, FORBIDDEN),
                Arguments.of(userUuid, authorities, "/catalog/1/card/*/tag/*", DELETE, FORBIDDEN),
                Arguments.of(userUuid, authorities, "/catalog/1/user", GET, FORBIDDEN),
                Arguments.of(userUuid, authorities, "/catalog/1/user", PUT, FORBIDDEN),
                Arguments.of(userUuid, authorities, "/catalog/1/user", DELETE, FORBIDDEN),
                Arguments.of(userUuid, authorities, "/catalog/1/upload", POST, FORBIDDEN),
                Arguments.of(userUuid, authorities, "/catalog/1/upload", PUT, FORBIDDEN),
                Arguments.of(userUuid, authorities, "/catalog/1/fs/*", GET, FORBIDDEN),
                Arguments.of(userUuid, authorities, "/catalog/1/fs/*", POST, OK),
                Arguments.of(userUuid, authorities, "/catalog/1/fs/*", PUT, OK),
                Arguments.of(userUuid, authorities, "/catalog/1/fs/*", DELETE, FORBIDDEN),
                Arguments.of(userUuid, authorities, "/catalog/1/fs/*/download", GET, FORBIDDEN)
        );
    }

    private static Stream<Arguments> getPermissionsMethodSource_FILE_SYSTEM_DELETE() {
        UUID userUuid = users.get(Authority.FILE_SYSTEM_DELETE.name());
        List<Authority> authorities = AccessServiceTest.authorities.get(Authority.FILE_SYSTEM_DELETE.name());

        return Stream.of(
                Arguments.of(userUuid, authorities, "/catalog", GET, OK),
                Arguments.of(userUuid, authorities, "/catalog", POST, OK),
                Arguments.of(userUuid, authorities, "/catalog/1", PUT, FORBIDDEN),
                Arguments.of(userUuid, authorities, "/catalog/1", DELETE, FORBIDDEN),
                Arguments.of(userUuid, authorities, "/catalog/1/field", GET, FORBIDDEN),
                Arguments.of(userUuid, authorities, "/catalog/1/field", POST, FORBIDDEN),
                Arguments.of(userUuid, authorities, "/catalog/1/field/*", PUT, FORBIDDEN),
                Arguments.of(userUuid, authorities, "/catalog/1/field/*", DELETE, FORBIDDEN),
                Arguments.of(userUuid, authorities, "/catalog/1/card", GET, FORBIDDEN),
                Arguments.of(userUuid, authorities, "/catalog/1/card", POST, FORBIDDEN),
                Arguments.of(userUuid, authorities, "/catalog/1/card/*", GET, FORBIDDEN),
                Arguments.of(userUuid, authorities, "/catalog/1/card/*", DELETE, FORBIDDEN),
                Arguments.of(userUuid, authorities, "/catalog/1/card/*/tag", PUT, FORBIDDEN),
                Arguments.of(userUuid, authorities, "/catalog/1/card/*/tag", DELETE, FORBIDDEN),
                Arguments.of(userUuid, authorities, "/catalog/1/card/*/tag/*", DELETE, FORBIDDEN),
                Arguments.of(userUuid, authorities, "/catalog/1/user", GET, FORBIDDEN),
                Arguments.of(userUuid, authorities, "/catalog/1/user", PUT, FORBIDDEN),
                Arguments.of(userUuid, authorities, "/catalog/1/user", DELETE, FORBIDDEN),
                Arguments.of(userUuid, authorities, "/catalog/1/upload", POST, FORBIDDEN),
                Arguments.of(userUuid, authorities, "/catalog/1/upload", PUT, FORBIDDEN),
                Arguments.of(userUuid, authorities, "/catalog/1/fs/*", GET, FORBIDDEN),
                Arguments.of(userUuid, authorities, "/catalog/1/fs/*", POST, FORBIDDEN),
                Arguments.of(userUuid, authorities, "/catalog/1/fs/*", PUT, FORBIDDEN),
                Arguments.of(userUuid, authorities, "/catalog/1/fs/*", DELETE, OK),
                Arguments.of(userUuid, authorities, "/catalog/1/fs/*/download", GET, FORBIDDEN)
        );
    }

    private static Stream<Arguments> getPermissionsMethodSource_ALL_AUTHORITIES_WITHOUT_MASTER() {
        UUID userUuid = users.get(ALL_AUTHORITIES_WITHOUT_MASTER);
        List<Authority> authorities = AccessServiceTest.authorities.get(ALL_AUTHORITIES_WITHOUT_MASTER);

        return Stream.of(
                Arguments.of(userUuid, authorities, "/catalog", GET, OK),
                Arguments.of(userUuid, authorities, "/catalog", POST, OK),
                Arguments.of(userUuid, authorities, "/catalog/1", PUT, FORBIDDEN),
                Arguments.of(userUuid, authorities, "/catalog/1", DELETE, FORBIDDEN),
                Arguments.of(userUuid, authorities, "/catalog/1/field", GET, OK),
                Arguments.of(userUuid, authorities, "/catalog/1/field", POST, OK),
                Arguments.of(userUuid, authorities, "/catalog/1/field/*", PUT, OK),
                Arguments.of(userUuid, authorities, "/catalog/1/field/*", DELETE, OK),
                Arguments.of(userUuid, authorities, "/catalog/1/card", GET, OK),
                Arguments.of(userUuid, authorities, "/catalog/1/card", POST, OK),
                Arguments.of(userUuid, authorities, "/catalog/1/card/*", GET, OK),
                Arguments.of(userUuid, authorities, "/catalog/1/card/*", DELETE, OK),
                Arguments.of(userUuid, authorities, "/catalog/1/card/*/tag", PUT, OK),
                Arguments.of(userUuid, authorities, "/catalog/1/card/*/tag", DELETE, OK),
                Arguments.of(userUuid, authorities, "/catalog/1/card/*/tag/*", DELETE, OK),
                Arguments.of(userUuid, authorities, "/catalog/1/user", GET, OK),
                Arguments.of(userUuid, authorities, "/catalog/1/user", PUT, OK),
                Arguments.of(userUuid, authorities, "/catalog/1/user", DELETE, OK),
                Arguments.of(userUuid, authorities, "/catalog/1/upload", POST, OK),
                Arguments.of(userUuid, authorities, "/catalog/1/upload", PUT, OK),
                Arguments.of(userUuid, authorities, "/catalog/1/fs/*", GET, OK),
                Arguments.of(userUuid, authorities, "/catalog/1/fs/*", POST, OK),
                Arguments.of(userUuid, authorities, "/catalog/1/fs/*", PUT, OK),
                Arguments.of(userUuid, authorities, "/catalog/1/fs/*", DELETE, OK),
                Arguments.of(userUuid, authorities, "/catalog/1/fs/*/download", GET, OK)
        );
    }


}
