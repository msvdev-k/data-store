package ru.msvdev.ds.server.security.access;

import lombok.Setter;
import org.springframework.http.HttpMethod;
import ru.msvdev.ds.server.security.Authority;
import ru.msvdev.ds.server.security.HttpRequest;
import ru.msvdev.ds.server.security.Permission;
import ru.msvdev.ds.server.security.AccessService;

import java.util.Set;


@Setter
public class MethodAccessTreeNode implements AccessService {

    private int level;
    private HttpMethod httpMethod;
    private Set<Authority> authorities;

    @Override
    public Permission getPermission(HttpRequest httpRequest) {
        if (level == httpRequest.pathParts().length && httpMethod == httpRequest.httpMethod()) {
            if (authorities.isEmpty()) return Permission.OK;

            Set<Authority> authorityIntersection = httpRequest.authorities();
            authorityIntersection.retainAll(authorities);

            return authorityIntersection.isEmpty() ? Permission.FORBIDDEN : Permission.OK;
        }

        return Permission.BAD_REQUEST;
    }
}
