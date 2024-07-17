package ru.msvdev.ds.server.security.access;

import lombok.Setter;
import ru.msvdev.ds.server.security.Authority;
import ru.msvdev.ds.server.security.HttpRequest;
import ru.msvdev.ds.server.security.Permission;
import ru.msvdev.ds.server.security.AuthorityService;

import java.util.List;

@Setter
public class CatalogIdAccessTreeNode extends RootAccessTreeNode {

    private int level;
    private AuthorityService authorityService;

    @Override
    public Permission getPermission(HttpRequest httpRequest) {
        String[] pathParts = httpRequest.pathParts();

        if (level < pathParts.length) {
            try {
                long catalogId = Long.parseLong(pathParts[level]);
                List<Authority> authorities = authorityService.loadAuthorities(httpRequest.userUuid(), catalogId);
                httpRequest.authorities().addAll(authorities);
                return super.getPermission(httpRequest);

            } catch (Exception e) {
                return Permission.BAD_REQUEST;
            }
        }

        return Permission.BAD_REQUEST;
    }
}
