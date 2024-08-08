package ru.msvdev.ds.server.base;

import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;


@ActiveProfiles("test")
public abstract class ApplicationTest {

    public static final PostgreSQLContainer<?> POSTGRESQL_CONTAINER;

    static {
        POSTGRESQL_CONTAINER = new PostgreSQLContainer<>("postgres:16.3-alpine");

        POSTGRESQL_CONTAINER.withReuse(true);
        POSTGRESQL_CONTAINER.withLabel("reuse.UUID", "a54682bc-9174-4732-9ef1-6620b6b1eb22");

        POSTGRESQL_CONTAINER.start();
    }

    @DynamicPropertySource
    public static void configureProperties(DynamicPropertyRegistry registry) {
        POSTGRESQL_CONTAINER.withUrlParam("stringtype", "unspecified");

        System.out.println("spring.datasource.url: " + POSTGRESQL_CONTAINER.getJdbcUrl());
        System.out.println("spring.datasource.username: " + POSTGRESQL_CONTAINER.getUsername());
        System.out.println("spring.datasource.password: " + POSTGRESQL_CONTAINER.getPassword());

        registry.add("spring.datasource.url", POSTGRESQL_CONTAINER::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRESQL_CONTAINER::getUsername);
        registry.add("spring.datasource.password", POSTGRESQL_CONTAINER::getPassword);
    }
}
