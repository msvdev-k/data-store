package ru.msvdev.ds.server.module.upload;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.transaction.annotation.Transactional;
import ru.msvdev.ds.server.base.ApplicationTest;
import ru.msvdev.ds.server.module.upload.base.UploadSessionState;
import ru.msvdev.ds.server.module.upload.service.UploadSessionService;

import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static ru.msvdev.ds.server.module.upload.base.UploadSessionState.*;


@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Sql(
        value = {"classpath:module/upload/upload-session-service-test.sql"},
        config = @SqlConfig(encoding = "UTF8")
)
public class UploadSessionServiceTest extends ApplicationTest {

    private final UploadSessionService uploadSessionService;
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UploadSessionServiceTest(UploadSessionService uploadSessionService, JdbcTemplate jdbcTemplate) {
        this.uploadSessionService = uploadSessionService;
        this.jdbcTemplate = jdbcTemplate;
    }


    @Test
    void findAllProcessingSessionId() {
        // region Given
        Set<Long> expectedId = Set.of(117L, 127L, 137L, 147L);
        // endregion


        // region When
        long[] actualId = uploadSessionService.findAllProcessingSessionId();
        // endregion


        // region Then
        assertEquals(expectedId, Arrays.stream(actualId).boxed().collect(Collectors.toSet()));
        // endregion
    }


    @ParameterizedTest
    @MethodSource
    void processing(long sessionId, UploadSessionState expectedState) throws NoSuchAlgorithmException {
        // region Given
        // endregion


        // region When
        uploadSessionService.processing(sessionId);
        // endregion


        // region Then
        if (expectedState == null) {
            String query = String.format("SELECT EXISTS(SELECT id FROM upload_sessions WHERE id = %d)", sessionId);
            assertNotEquals(Boolean.TRUE, jdbcTemplate.queryForObject(query, Boolean.class));

        } else {
            String query = String.format("SELECT state FROM upload_sessions WHERE id = %d", sessionId);
            UploadSessionState actualState = jdbcTemplate.queryForObject(query, UploadSessionState.class);
            assertEquals(expectedState, actualState);
        }
        // endregion
    }

    private static Stream<Arguments> processing() {
        return Stream.of(
                Arguments.of(117, null),
                Arguments.of(127, UPLOAD),
                Arguments.of(137, null),
                Arguments.of(147, ERROR)
        );
    }
}