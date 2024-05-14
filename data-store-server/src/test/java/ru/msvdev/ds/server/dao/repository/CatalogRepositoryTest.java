package ru.msvdev.ds.server.dao.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import ru.msvdev.ds.server.base.ApplicationTest;
import ru.msvdev.ds.server.dao.entity.Catalog;
import ru.msvdev.ds.server.security.Authority;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;


@DataJdbcTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CatalogRepositoryTest extends ApplicationTest {

    private final CatalogRepository catalogRepository;

    @Autowired
    public CatalogRepositoryTest(CatalogRepository catalogRepository) {
        this.catalogRepository = catalogRepository;
    }

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }


    @Test
    void crudTest() {
        String name1 = "Каталог 1";

        String name2 = "Каталог 2";
        String description2 = "Описание каталога";


        // = insert =====================

        Catalog insertedCatalog1 = catalogRepository.insert(name1, null);
        Catalog insertedCatalog2 = catalogRepository.insert(name2, description2);

        assertTrue(insertedCatalog1.id() > 0);
        assertEquals(name1, insertedCatalog1.name());
        assertNull(insertedCatalog1.description());

        assertTrue(insertedCatalog2.id() > 0);
        assertEquals(name2, insertedCatalog2.name());
        assertEquals(description2, insertedCatalog2.description());

        System.out.println(insertedCatalog1);
        System.out.println(insertedCatalog2);

        // = findById ===================

        Optional<Catalog> catalogOptional1 = catalogRepository.findById(insertedCatalog1.id());
        Optional<Catalog> catalogOptional2 = catalogRepository.findById(insertedCatalog2.id());

        assertTrue(catalogOptional1.isPresent());
        assertTrue(catalogOptional2.isPresent());
        assertEquals(insertedCatalog1, catalogOptional1.get());
        assertEquals(insertedCatalog2, catalogOptional2.get());

        // = updateName =================

        String newName = "Новое название каталога";
        assertTrue(catalogRepository.updateName(insertedCatalog1.id(), newName));

        Optional<Catalog> optional = catalogRepository.findById(insertedCatalog1.id());
        assertTrue(optional.isPresent());
        assertEquals(optional.get().name(), newName);

        // = updateDescription ==========

        String newDescription = "Новое описание каталога";
        assertTrue(catalogRepository.updateDescription(insertedCatalog1.id(), newDescription));

        optional = catalogRepository.findById(insertedCatalog1.id());
        assertTrue(optional.isPresent());
        assertEquals(optional.get().description(), newDescription);

        // = deleteById =================

        assertTrue(catalogRepository.deleteById(insertedCatalog1.id()));
        assertTrue(catalogRepository.deleteById(insertedCatalog2.id()));

        catalogOptional1 = catalogRepository.findById(insertedCatalog1.id());
        catalogOptional2 = catalogRepository.findById(insertedCatalog2.id());

        assertTrue(catalogOptional1.isEmpty());
        assertTrue(catalogOptional2.isEmpty());

    }


    @Test
    void authorityTest() {
        UUID userUuid = UUID.randomUUID();

        Catalog catalog = catalogRepository.insert("Картотека", null);
        long catalogId = catalog.id();

        Authority[] authorityTypes = Authority.values();

        // = addAuthority ===============

        for (Authority type : authorityTypes) {
            assertTrue(catalogRepository.addAuthority(catalogId, userUuid, type));
        }

        // = findAllAuthoritiesAsString =

        List<Authority> authorities = catalogRepository.findAllAuthorities(catalogId, userUuid);

        assertEquals(authorityTypes.length, authorities.size());

        for (Authority type : authorityTypes) {
            assertTrue(authorities.contains(type));
        }

        // = removeAuthority ============

        for (Authority type : authorityTypes) {
            assertTrue(catalogRepository.removeAuthority(catalogId, userUuid, type));
        }

        authorities = catalogRepository.findAllAuthorities(catalogId, userUuid);
        assertTrue(authorities.isEmpty());

        // = removeAllAuthorities =======

        for (Authority type : authorityTypes) {
            assertTrue(catalogRepository.addAuthority(catalogId, userUuid, type));
        }
        assertTrue(catalogRepository.removeAllAuthorities(catalogId, userUuid));

        authorities = catalogRepository.findAllAuthorities(catalogId, userUuid);
        assertTrue(authorities.isEmpty());
    }


    @Test
    void findAllTest() {
        UUID userUuid1 = UUID.randomUUID();
        UUID userUuid2 = UUID.randomUUID();

        Catalog catalog1 = catalogRepository.insert("Картотека 1", null);
        Catalog catalog2 = catalogRepository.insert("Картотека 2", null);
        Catalog catalog3 = catalogRepository.insert("Картотека 3", null);

        catalogRepository.addAuthority(catalog1.id(), userUuid1, Authority.MASTER);
        catalogRepository.addAuthority(catalog2.id(), userUuid1, Authority.MASTER);
        catalogRepository.addAuthority(catalog3.id(), userUuid1, Authority.MASTER);

        catalogRepository.addAuthority(catalog1.id(), userUuid2, Authority.READING);

        catalogRepository.addAuthority(catalog2.id(), userUuid2, Authority.WRITING);
        catalogRepository.addAuthority(catalog2.id(), userUuid2, Authority.FIELD_TEMPLATE_WRITING);

        // = findAllUsers ===============

        List<UUID> catalog1users = catalogRepository.findAllUsers(catalog1.id());
        List<UUID> catalog2users = catalogRepository.findAllUsers(catalog2.id());
        List<UUID> catalog3users = catalogRepository.findAllUsers(catalog3.id());

        assertEquals(2, catalog1users.size());
        assertEquals(2, catalog2users.size());
        assertEquals(1, catalog3users.size());

        assertTrue(catalog1users.contains(userUuid1));
        assertTrue(catalog1users.contains(userUuid2));

        assertTrue(catalog2users.contains(userUuid1));
        assertTrue(catalog2users.contains(userUuid2));

        assertTrue(catalog3users.contains(userUuid1));
        assertFalse(catalog3users.contains(userUuid2));

        // = findAll ====================

        List<Catalog> user1Catalogs = catalogRepository.findAll(userUuid1);

        assertEquals(3, user1Catalogs.size());
        for (Catalog catalog : user1Catalogs) {
            assertEquals(1, catalog.authorities().length);
            assertEquals(Authority.MASTER, catalog.authorities()[0]);
        }

        List<Catalog> user2Catalogs = catalogRepository.findAll(userUuid2);

        assertEquals(2, user2Catalogs.size());
        for (Catalog catalog : user2Catalogs) {
            if (catalog.id() == catalog1.id()) {
                assertEquals(1, catalog.authorities().length);
                assertEquals(Authority.READING, catalog.authorities()[0]);
                continue;
            }
            if (catalog.id() == catalog2.id()) {
                assertEquals(2, catalog.authorities().length);
                List<Authority> authorities = Arrays.asList(catalog.authorities());
                assertTrue(authorities.contains(Authority.WRITING));
                assertTrue(authorities.contains(Authority.FIELD_TEMPLATE_WRITING));
            }
        }

        user1Catalogs.forEach(System.out::println);
        user2Catalogs.forEach(System.out::println);
    }

}