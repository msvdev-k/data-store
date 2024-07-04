package ru.msvdev.ds.server.dao.repository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import ru.msvdev.ds.server.base.ApplicationTest;
import ru.msvdev.ds.server.dao.entity.Catalog;
import ru.msvdev.ds.server.security.Authority;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;


@DataJdbcTest
@Sql(
        value = {"classpath:db/repository/catalog-repository-test.sql"},
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
    @CsvSource({
            "Каталог, Описание каталога",
            "Catalog, Description",
            "Название, null"
    })
    void insertCatalogTest(String catalogName, String catalogDescription) {
        // region Given
        if (catalogDescription.equals("null")) catalogDescription = null;
        // endregion


        // region When
        Catalog insertedCatalog = catalogRepository.insert(catalogName, catalogDescription);
        // endregion


        // region Then
        assertTrue(insertedCatalog.id() > 36);
        assertEquals(catalogName, insertedCatalog.name());
        assertEquals(catalogDescription, insertedCatalog.description());
        if (catalogDescription == null) {
            assertNull(insertedCatalog.description());
        }
        assertNull(insertedCatalog.authorities());
        //endregion
    }


    @ParameterizedTest
    @CsvSource({
            "1, Название каталога, Описание каталога",
            "2, Книги, Каталог книг",
            "3, Books, Book catalog",
            "4, Рецепты, null"
    })
    void findCatalogByIdTest(int id, String name, String description) {
        // region Given
        if (description.equals("null")) description = null;
        // endregion


        // region When
        Catalog catalog = catalogRepository.findById(id);
        // endregion


        // region Then
        assertEquals(id, catalog.id());
        assertEquals(name, catalog.name());
        assertEquals(description, catalog.description());
        assertNull(catalog.authorities());
        //endregion
    }


    @ParameterizedTest
    @CsvSource({
            "1, Новый каталог",
            "2, New catalog",
            "3, New Books",
            "4, Новые рецепты"
    })
    void updateCatalogNameTest(int id, String newName) {
        // region Given

        // endregion


        // region When
        boolean updateFlag = catalogRepository.updateName(id, newName);
        // endregion


        // region Then
        assertTrue(updateFlag);

        Catalog catalog = catalogRepository.findById(id);
        assertEquals(id, catalog.id());
        assertEquals(newName, catalog.name());
        //endregion
    }


    @ParameterizedTest
    @CsvSource({
            "1, Новое описание",
            "2, Каталог новых книг",
            "3, New description",
            "4, null"
    })
    void updateCatalogDescriptionTest(int id, String newDescription) {
        // region Given
        if (newDescription.equals("null")) newDescription = null;
        // endregion


        // region When
        boolean updateFlag = catalogRepository.updateDescription(id, newDescription);
        // endregion


        // region Then
        assertTrue(updateFlag);

        Catalog catalog = catalogRepository.findById(id);
        assertEquals(id, catalog.id());
        assertEquals(newDescription, catalog.description());
        //endregion
    }


    @Test
    void deleteCatalogByIdTest() {
        // region Given
        int id = 1;
        // endregion


        // region When
        boolean deleteFlag = catalogRepository.deleteById(id);
        // endregion


        // region Then
        assertTrue(deleteFlag);

        Catalog catalog = catalogRepository.findById(id);
        assertNull(catalog);
        //endregion
    }


    @Test
    void findAllCatalogsTest() {
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
    void findAllUsersTest() {
        // region Given
        int catalogId = 1;
        // endregion


        // region When
        List<UUID> users = catalogRepository.findAllUsers(catalogId);
        // endregion


        // region Then
        assertEquals(3, users.size());

        assertTrue(users.contains(UUID.fromString("bfe5e92a-ba1f-4412-a5e9-2aba1fc41272")));
        assertTrue(users.contains(UUID.fromString("42b16c80-7987-462b-b16c-807987062be1")));
        assertTrue(users.contains(UUID.fromString("64f25d2f-953f-4605-b25d-2f953f260558")));
        //endregion
    }


    @Test
    void findAllAuthoritiesTest() {
        // region Given
        int catalogId = 2;
        UUID userUUID = UUID.fromString("bfe5e92a-ba1f-4412-a5e9-2aba1fc41272");
        Authority[] authorityTypes = Arrays.stream(Authority.values())
                .filter(authority -> authority != Authority.MASTER)
                .toArray(Authority[]::new);
        // endregion


        // region When
        List<Authority> authorities = catalogRepository.findAllAuthorities(catalogId, userUUID);
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
    void addAuthorityToCatalogTest(Authority authority) {
        // region Given
        int catalogId = 3;
        UUID userUUID = UUID.fromString("bfe5e92a-ba1f-4412-a5e9-2aba1fc41272");
        // endregion


        // region When
        boolean addFlag = catalogRepository.addAuthority(catalogId, userUUID, authority);
        // endregion


        // region Then
        assertTrue(addFlag);

        List<Authority> authorities = catalogRepository.findAllAuthorities(catalogId, userUUID);
        assertEquals(1, authorities.size());
        assertTrue(authorities.contains(authority));
        //endregion
    }


    @ParameterizedTest
    @EnumSource(value = Authority.class, names = {"MASTER"}, mode = EnumSource.Mode.EXCLUDE)
    void removeAuthorityFromCatalogTest(Authority authority) {
        // region Given
        int catalogId = 2;
        UUID userUUID = UUID.fromString("bfe5e92a-ba1f-4412-a5e9-2aba1fc41272");
        Authority[] authorityTypes = Arrays.stream(Authority.values())
                .filter(a -> a != Authority.MASTER && a != authority)
                .toArray(Authority[]::new);
        // endregion


        // region When
        boolean removeFlag = catalogRepository.removeAuthority(catalogId, userUUID, authority);
        // endregion


        // region Then
        assertTrue(removeFlag);

        List<Authority> authorities = catalogRepository.findAllAuthorities(catalogId, userUUID);
        assertEquals(authorityTypes.length, authorities.size());
        for (Authority a : authorityTypes) {
            assertTrue(authorities.contains(a));
        }
        //endregion
    }


    @Test
    void removeAllAuthorityFromCatalogTest() {
        // region Given
        int catalogId = 2;
        UUID userUUID = UUID.fromString("bfe5e92a-ba1f-4412-a5e9-2aba1fc41272");
        // endregion


        // region When
        boolean removeFlag = catalogRepository.removeAllAuthorities(catalogId, userUUID);
        // endregion


        // region Then
        assertTrue(removeFlag);

        List<Authority> authorities = catalogRepository.findAllAuthorities(catalogId, userUUID);
        assertTrue(authorities.isEmpty());
        //endregion
    }
}