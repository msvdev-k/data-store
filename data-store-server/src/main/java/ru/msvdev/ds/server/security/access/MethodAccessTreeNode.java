package ru.msvdev.ds.server.security.access;

import lombok.Setter;
import org.springframework.http.HttpMethod;
import ru.msvdev.ds.server.security.Authority;
import ru.msvdev.ds.server.security.HttpRequest;
import ru.msvdev.ds.server.security.Permission;
import ru.msvdev.ds.server.security.UserAccessService;

import java.util.Set;


@Setter
public class MethodAccessTreeNode implements UserAccessService {

    private HttpMethod httpMethod;
    private Set<Authority> authorities;

    @Override
    public Permission getPermission(HttpRequest httpRequest) {
        if (httpMethod == httpRequest.httpMethod()) {
            if (authorities.isEmpty()) return Permission.OK;

            Set<Authority> authorityIntersection = httpRequest.authorities();
            authorityIntersection.retainAll(authorities);

            return authorityIntersection.isEmpty() ? Permission.FORBIDDEN : Permission.OK;
        }

        return Permission.BAD_REQUEST;
    }
}
