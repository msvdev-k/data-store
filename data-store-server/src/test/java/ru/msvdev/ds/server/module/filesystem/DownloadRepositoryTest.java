package ru.msvdev.ds.server.module.filesystem;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import ru.msvdev.ds.server.base.ApplicationTest;
import ru.msvdev.ds.server.module.filesystem.entity.FileChunk;
import ru.msvdev.ds.server.module.filesystem.repository.DownloadRepository;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;


@DataJdbcTest
@Sql(
        value = {"classpath:module/filesystem/download-repository-test.sql"},
        config = @SqlConfig(encoding = "UTF8")
)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class DownloadRepositoryTest extends ApplicationTest {

    private final DownloadRepository downloadRepository;

    @Autowired
    public DownloadRepositoryTest(DownloadRepository downloadRepository) {
        this.downloadRepository = downloadRepository;
    }


    private static long catalogId;


    @BeforeAll
    static void beforeAll() {
        catalogId = 115;
    }


    @ParameterizedTest
    @MethodSource
    void findById(long nodeId, int chunkNumber, FileChunk expectedFileChunk) {
        // region Given
        // endregion


        // region When
        FileChunk fileChunk = downloadRepository.findChunk(catalogId, nodeId, chunkNumber);
        // endregion


        // region Then
        assertEquals(expectedFileChunk, fileChunk);
        // endregion
    }

    private static Stream<Arguments> findById() {
        return Stream.of(
                Arguments.of(61, 1,
                        new FileChunk("f20aacfc293a9b8c8d0345960ba7b36035122d726abda130c25d5116eebe7437",
                                204, 6, 32, 44, 1,
                                "87+9PGogZ92e6N1UIDXIlVWEZaMEVVfFUwGGnXPFD30=")
                ),
                Arguments.of(61, 2,
                        new FileChunk("f20aacfc293a9b8c8d0345960ba7b36035122d726abda130c25d5116eebe7437",
                                204, 6, 32, 44, 2,
                                "h9XlAs/r+amjFSJqj8LrwGwwuGWn8Lexar9w5jMh9NY=")
                ),
                Arguments.of(61, 3,
                        new FileChunk("f20aacfc293a9b8c8d0345960ba7b36035122d726abda130c25d5116eebe7437",
                                204, 6, 32, 44, 3,
                                "WebxGrW8K6kUHX8Peno2MdQL4kTdt5O19mdDt3TsVQY=")
                ),
                Arguments.of(61, 4,
                        new FileChunk("f20aacfc293a9b8c8d0345960ba7b36035122d726abda130c25d5116eebe7437",
                                204, 6, 32, 44, 4,
                                "nw/ZEno9K/D7DOB08u9v1fQ9JHlNmOCugytt96E3GGA=")
                ),
                Arguments.of(61, 5,
                        new FileChunk("f20aacfc293a9b8c8d0345960ba7b36035122d726abda130c25d5116eebe7437",
                                204, 6, 32, 44, 5,
                                "EbQn6rFJ2pqHnqAWWexKX/aHJXkUEK0NAPSKl7faVdY=")
                ),
                Arguments.of(61, 6,
                        new FileChunk("f20aacfc293a9b8c8d0345960ba7b36035122d726abda130c25d5116eebe7437",
                                204, 6, 32, 44, 6,
                                "bCjpAqJgR1aJ9iFe6GWuvUk5etZCSK5FQq23BMdTswjyb2rAxefytPj1Bgs=")
                ),


                Arguments.of(62, 1,
                        new FileChunk("4db703781440523ad9321ee163b1b4ad7d910b3b68f8c7c6348a4c224668b4ef",
                                107, 3, 32, 43, 1,
                                "UfY3UhptwqSGQHc4zQKhJqqaFztSmzguV8qgfBhLwGc=")
                ),
                Arguments.of(62, 2,
                        new FileChunk("4db703781440523ad9321ee163b1b4ad7d910b3b68f8c7c6348a4c224668b4ef",
                                107, 3, 32, 43, 2,
                                "TteiKVEIPeAK2j8GOltSKTWkH/PUxenwFD2wVAZW9Ug=")
                ),
                Arguments.of(62, 3,
                        new FileChunk("4db703781440523ad9321ee163b1b4ad7d910b3b68f8c7c6348a4c224668b4ef",
                                107, 3, 32, 43, 3,
                                "a1yRgGzg3sZdWPKbdH8/X1rvP2BvccXIIgWLBrghtUZPYqquL1ydShvYiQ==")
                )
        );
    }

}
