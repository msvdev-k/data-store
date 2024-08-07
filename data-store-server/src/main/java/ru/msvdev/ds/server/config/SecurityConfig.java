package ru.msvdev.ds.server.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.msvdev.ds.server.security.AccessService;
import ru.msvdev.ds.server.security.access.CatalogBasedAccessService;
import ru.msvdev.ds.server.security.AuthorityService;

import static org.springframework.http.HttpMethod.*;
import static ru.msvdev.ds.server.security.Authority.*;


@Configuration
public class SecurityConfig {

    @Bean
    public AccessService catalogAccessService(AuthorityService authorityService) {
        return CatalogBasedAccessService.builder()
                .setAuthorityService(authorityService)

                .addMatcher("catalog", GET)
                .addMatcher("catalog", POST)
                .addMatcher("catalog/{catalogId}", PUT, MASTER)
                .addMatcher("catalog/{catalogId}", DELETE, MASTER)
                .addMatcher("catalog/{catalogId}/field", GET, MASTER, READING, FIELD_TEMPLATE_WRITING)
                .addMatcher("catalog/{catalogId}/field", POST, MASTER, FIELD_TEMPLATE_WRITING)
                .addMatcher("catalog/{catalogId}/field/*", PUT, MASTER, FIELD_TEMPLATE_WRITING)
                .addMatcher("catalog/{catalogId}/field/*", DELETE, MASTER, FIELD_TEMPLATE_DELETING)
                .addMatcher("catalog/{catalogId}/card", GET, MASTER, READING)
                .addMatcher("catalog/{catalogId}/card", POST, MASTER, WRITING)
                .addMatcher("catalog/{catalogId}/card/*", GET, MASTER, READING)
                .addMatcher("catalog/{catalogId}/card/*", DELETE, MASTER, DELETING)
                .addMatcher("catalog/{catalogId}/card/*/tag", PUT, MASTER, WRITING)
                .addMatcher("catalog/{catalogId}/card/*/tag", DELETE, MASTER, DELETING)
                .addMatcher("catalog/{catalogId}/card/*/tag/*", DELETE, MASTER, WRITING)
                .addMatcher("catalog/{catalogId}/user", GET, MASTER, GRANT_AUTHORITY)
                .addMatcher("catalog/{catalogId}/user", PUT, MASTER, GRANT_AUTHORITY)
                .addMatcher("catalog/{catalogId}/user", DELETE, MASTER, GRANT_AUTHORITY)
                .addMatcher("catalog/{catalogId}/upload", POST, MASTER, FILE_UPLOAD)
                .addMatcher("catalog/{catalogId}/upload", PUT, MASTER, FILE_UPLOAD)
                .addMatcher("catalog/{catalogId}/fs/*", GET, MASTER, FILE_SYSTEM_READ)
                .addMatcher("catalog/{catalogId}/fs/*", POST, MASTER, FILE_SYSTEM_WRITE)
                .addMatcher("catalog/{catalogId}/fs/*", PUT, MASTER, FILE_SYSTEM_WRITE)
                .addMatcher("catalog/{catalogId}/fs/*", DELETE, MASTER, FILE_SYSTEM_DELETE)
                .addMatcher("catalog/{catalogId}/fs/*/download", GET, MASTER, FILE_DOWNLOAD)

                .build();
    }

}
