package ru.msvdev.ds.server.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import ru.msvdev.ds.server.security.UserAccessService;
import ru.msvdev.ds.server.security.Authority;
import ru.msvdev.ds.server.security.access.CatalogBaseAccessService;
import ru.msvdev.ds.server.security.UserAuthorityService;


@Configuration
public class SecurityConfig {

    @Bean
    public UserAccessService catalogAccessService(UserAuthorityService userAuthorityService) {
        return CatalogBaseAccessService.builder()
                .setAuthorityService(userAuthorityService)

                .addMatcher("catalog", HttpMethod.GET)
                .addMatcher("catalog", HttpMethod.POST)
                .addMatcher("catalog/{catalogId}", HttpMethod.PUT, Authority.MASTER)
                .addMatcher("catalog/{catalogId}", HttpMethod.DELETE, Authority.MASTER)
                .addMatcher("catalog/{catalogId}/field", HttpMethod.GET, Authority.MASTER, Authority.READING)
                .addMatcher("catalog/{catalogId}/field", HttpMethod.POST, Authority.MASTER, Authority.FIELD_TEMPLATE_WRITING)
                .addMatcher("catalog/{catalogId}/field/*", HttpMethod.PUT, Authority.MASTER, Authority.FIELD_TEMPLATE_WRITING)
                .addMatcher("catalog/{catalogId}/field/*", HttpMethod.DELETE, Authority.MASTER, Authority.FIELD_TEMPLATE_DELETING)
                .addMatcher("catalog/{catalogId}/card", HttpMethod.GET, Authority.MASTER, Authority.READING)
                .addMatcher("catalog/{catalogId}/card", HttpMethod.POST, Authority.MASTER, Authority.WRITING)
                .addMatcher("catalog/{catalogId}/card/*", HttpMethod.GET, Authority.MASTER, Authority.READING)
                .addMatcher("catalog/{catalogId}/card/*", HttpMethod.DELETE, Authority.MASTER, Authority.DELETING)
                .addMatcher("catalog/{catalogId}/card/*/tag", HttpMethod.PUT, Authority.MASTER, Authority.WRITING)
                .addMatcher("catalog/{catalogId}/card/*/tag", HttpMethod.DELETE, Authority.MASTER, Authority.WRITING)
                .addMatcher("catalog/{catalogId}/card/*/tag/*", HttpMethod.DELETE, Authority.MASTER, Authority.WRITING)
                .addMatcher("catalog/{catalogId}/user", HttpMethod.GET, Authority.MASTER, Authority.GRANT_AUTHORITY)
                .addMatcher("catalog/{catalogId}/user", HttpMethod.PUT, Authority.MASTER, Authority.GRANT_AUTHORITY)
                .addMatcher("catalog/{catalogId}/user", HttpMethod.DELETE, Authority.MASTER, Authority.GRANT_AUTHORITY)

                .build();
    }

}
