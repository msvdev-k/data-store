package ru.msvdev.ds.server.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import ru.msvdev.ds.server.security.AccessService;
import ru.msvdev.ds.server.security.access.CatalogBasedAccessService;
import ru.msvdev.ds.server.security.AuthorityService;

import static ru.msvdev.ds.server.security.Authority.*;


@Configuration
public class SecurityConfig {

    @Bean
    public AccessService catalogAccessService(AuthorityService authorityService) {
        return CatalogBasedAccessService.builder()
                .setAuthorityService(authorityService)

                .addMatcher("catalog", HttpMethod.GET)
                .addMatcher("catalog", HttpMethod.POST)
                .addMatcher("catalog/{catalogId}", HttpMethod.PUT, MASTER)
                .addMatcher("catalog/{catalogId}", HttpMethod.DELETE, MASTER)
                .addMatcher("catalog/{catalogId}/field", HttpMethod.GET, MASTER, READING)
                .addMatcher("catalog/{catalogId}/field", HttpMethod.POST, MASTER, FIELD_TEMPLATE_WRITING)
                .addMatcher("catalog/{catalogId}/field/*", HttpMethod.PUT, MASTER, FIELD_TEMPLATE_WRITING)
                .addMatcher("catalog/{catalogId}/field/*", HttpMethod.DELETE, MASTER, FIELD_TEMPLATE_DELETING)
                .addMatcher("catalog/{catalogId}/card", HttpMethod.GET, MASTER, READING)
                .addMatcher("catalog/{catalogId}/card", HttpMethod.POST, MASTER, WRITING)
                .addMatcher("catalog/{catalogId}/card/*", HttpMethod.GET, MASTER, READING)
                .addMatcher("catalog/{catalogId}/card/*", HttpMethod.DELETE, MASTER, DELETING)
                .addMatcher("catalog/{catalogId}/card/*/tag", HttpMethod.PUT, MASTER, WRITING)
                .addMatcher("catalog/{catalogId}/card/*/tag", HttpMethod.DELETE, MASTER, WRITING)
                .addMatcher("catalog/{catalogId}/card/*/tag/*", HttpMethod.DELETE, MASTER, WRITING)
                .addMatcher("catalog/{catalogId}/user", HttpMethod.GET, MASTER, GRANT_AUTHORITY)
                .addMatcher("catalog/{catalogId}/user", HttpMethod.PUT, MASTER, GRANT_AUTHORITY)
                .addMatcher("catalog/{catalogId}/user", HttpMethod.DELETE, MASTER, GRANT_AUTHORITY)
                .addMatcher("catalog/{catalogId}/upload", HttpMethod.POST, MASTER, FILE_UPLOAD)
                .addMatcher("catalog/{catalogId}/upload", HttpMethod.PUT, MASTER, FILE_UPLOAD)
                .addMatcher("catalog/{catalogId}/fs/*", HttpMethod.GET, MASTER)
                .addMatcher("catalog/{catalogId}/fs/*", HttpMethod.POST, MASTER)
                .addMatcher("catalog/{catalogId}/fs/*", HttpMethod.PUT, MASTER)
                .addMatcher("catalog/{catalogId}/fs/*", HttpMethod.DELETE, MASTER)
                .addMatcher("catalog/{catalogId}/fs/*/download", HttpMethod.GET, MASTER)

                .build();
    }

}
