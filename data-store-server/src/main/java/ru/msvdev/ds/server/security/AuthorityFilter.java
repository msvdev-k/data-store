package ru.msvdev.ds.server.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;


@Component
@RequiredArgsConstructor
public class AuthorityFilter extends OncePerRequestFilter {

    private static final String USER_UUID_HEADER = "User-UUID";

    private final UserAccessService userAccessService;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) {
        try {
            String userUuidHeader = request.getHeader(USER_UUID_HEADER);
            if (userUuidHeader == null) {
                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                return;
            }

            UUID userUuid = UUID.fromString(userUuidHeader);
            String[] pathParts = request.getPathInfo().substring(1).split("/");
            HttpMethod httpMethod = HttpMethod.valueOf(request.getMethod());
            Set<Authority> authorities = new HashSet<>();

            HttpRequest httpRequest = new HttpRequest(userUuid, pathParts, httpMethod, authorities);
            Permission permission = userAccessService.getPermission(httpRequest);

            switch (permission) {
                case OK -> filterChain.doFilter(request, response);
                case FORBIDDEN -> response.setStatus(HttpStatus.FORBIDDEN.value());
                case BAD_REQUEST -> response.setStatus(HttpStatus.BAD_REQUEST.value());
            }

        } catch (Exception e) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
        }
    }
}
