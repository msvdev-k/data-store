package ru.msvdev.ds.server.security.access;

import lombok.Setter;
import ru.msvdev.ds.server.security.HttpRequest;
import ru.msvdev.ds.server.security.Permission;

@Setter
public class PathAccessTreeNode extends RootAccessTreeNode {

    private int level;
    private String path;

    @Override
    public Permission getPermission(HttpRequest httpRequest) {
        String[] pathParts = httpRequest.pathParts();

        if (level < pathParts.length && path.equalsIgnoreCase(pathParts[level])) {
            return super.getPermission(httpRequest);
        }

        return Permission.BAD_REQUEST;
    }
}
