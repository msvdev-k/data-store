package ru.msvdev.ds.server.module.catalog;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import ru.msvdev.ds.server.base.ApplicationTest;
import ru.msvdev.ds.server.module.catalog.entity.Catalog;
import ru.msvdev.ds.server.module.catalog.repository.CatalogRepository;
import ru.msvdev.ds.server.security.Authority;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;


@DataJdbcTest
@Sql(
        value = {"classpath:module/catalog/catalog-repository-test.sql"},
        config = @SqlConfig(encoding = "UTF8")
)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CatalogRepositoryTest extends ApplicationTest {

    private final CatalogRepository catalogRepository;

    @Autowired
    public CatalogRepositoryTest(CatalogRepository catalogRepository) {
        this.catalogRepository = catalogRepository;
    }


    @ParameterizedTest
    @MethodSource
    void findById(long id, UUID userUuid, Catalog expectedCatalog) {
        // region Given
        List<Authority> expectedAuthorities = Arrays.asList(expectedCatalog.authorities());
        // endregion


        // region When
        Catalog catalog = catalogRepository.findById(userUuid, id);
        // endregion


        // region Then
        assertEquals(expectedCatalog.id(), catalog.id());
        assertEquals(expectedCatalog.name(), catalog.name());
        assertEquals(expectedCatalog.description(), catalog.description());
        assertEquals(expectedAuthorities.size(), catalog.authorities().length);
        for (Authority authority : catalog.authorities()) {
            assertTrue(expectedAuthorities.contains(authority));
        }
        //endregion
    }

    private static Stream<Arguments> findById() {
        return Stream.of(
                Arguments.of(1, "bfe5e92a-ba1f-4412-a5e9-2aba1fc41272",
                        new Catalog(
                                1,
                                "Название каталога",
                                "Описание каталога",
                                new Authority[]{Authority.MASTER})
                ),
                Arguments.of(2, "bfe5e92a-ba1f-4412-a5e9-2aba1fc41272",
                        new Catalog(
                                2,
                                "Книги",
                                "Каталог книг",
                                new Authority[]{
                                        Authority.GRANT_AUTHORITY,
                                        Authority.READING,
                                        Authority.WRITING,
                                        Authority.DELETING,
                                        Authority.FIELD_TEMPLATE_WRITING,
                                        Authority.FIELD_TEMPLATE_DELETING,
                                        Authority.FILE_UPLOAD,
                                        Authority.FILE_DOWNLOAD,
                                        Authority.FILE_SYSTEM_READ,
                                        Authority.FILE_SYSTEM_WRITE,
                                        Authority.FILE_SYSTEM_DELETE
                                })
                ),
                Arguments.of(2, "42b16c80-7987-462b-b16c-807987062be1",
                        new Catalog(
                                2,
                                "Книги",
                                "Каталог книг",
                                new Authority[]{Authority.MASTER})
                ),
                Arguments.of(1, "42b16c80-7987-462b-b16c-807987062be1",
                        new Catalog(
                                1,
                                "Название каталога",
                                "Описание каталога",
                                new Authority[]{
                                        Authority.GRANT_AUTHORITY,
                                        Authority.READING,
                                        Authority.WRITING,
                                        Authority.DELETING,
                                        Authority.FIELD_TEMPLATE_WRITING,
                                        Authority.FIELD_TEMPLATE_DELETING,
                                        Authority.FILE_UPLOAD,
                                        Authority.FILE_DOWNLOAD,
                                        Authority.FILE_SYSTEM_READ,
                                        Authority.FILE_SYSTEM_WRITE,
                                        Authority.FILE_SYSTEM_DELETE
                                })
                ),
                Arguments.of(1, "64f25d2f-953f-4605-b25d-2f953f260558",
                        new Catalog(
                                1,
                                "Название каталога",
                                "Описание каталога",
                                new Authority[]{Authority.READING})
                )
        );
    }


    @Test
    void findAll() {
        // region Given
        UUID userUUID = UUID.fromString("bfe5e92a-ba1f-4412-a5e9-2aba1fc41272");
        // endregion


        // region When
        List<Catalog> catalogs = catalogRepository.findAll(userUUID);
        // endregion


        // region Then
        assertEquals(2, catalogs.size());
        //endregion
    }


    @Test
    void insert() {
        // region Given
        UUID userUUID = UUID.fromString("bfe5e92a-ba1f-4412-a5e9-2aba1fc41272");

        long catalogId = 37;
        String catalogName = "Новый каталог";
        // endregion


        // region When
        Catalog insertedCatalog = catalogRepository.insert(
                userUUID, catalogName, null, Authority.MASTER
        );
        // endregion


        // region Then
        assertNotNull(insertedCatalog);
        assertEquals(catalogId, insertedCatalog.id());
        assertEquals(catalogName, insertedCatalog.name());
        assertNull(insertedCatalog.description());
        assertEquals(1, insertedCatalog.authorities().length);
        assertEquals(Authority.MASTER, insertedCatalog.authorities()[0]);
        // endregion
    }


    @ParameterizedTest
    @CsvSource({
            "1, Новый каталог",
            "2, New catalog",
            "3, New Books",
            "4, Новые рецепты"
    })
    void updateName(int id, String newName) {
        // region Given
        // endregion


        // region When
        boolean updateFlag = catalogRepository.updateName(id, newName);
        // endregion


        // region Then
        assertTrue(updateFlag);
        //endregion
    }


    @ParameterizedTest
    @CsvSource({
            "1, Новое описание",
            "2, Каталог новых книг",
            "3, New description",
            "4, null"
    })
    void updateDescription(int id, String newDescription) {
        // region Given
        if (newDescription.equals("null")) newDescription = null;
        // endregion


        // region When
        boolean updateFlag = catalogRepository.updateDescription(id, newDescription);
        // endregion


        // region Then
        assertTrue(updateFlag);
        //endregion
    }


    @ParameterizedTest
    @ValueSource(longs = {1, 2, 3, 4})
    void deleteCatalogByIdTest(long catalogId) {
        // region Given
        // endregion


        // region When
        boolean deleteFlag = catalogRepository.deleteById(catalogId);
        // endregion


        // region Then
        assertTrue(deleteFlag);
        //endregion
    }

}