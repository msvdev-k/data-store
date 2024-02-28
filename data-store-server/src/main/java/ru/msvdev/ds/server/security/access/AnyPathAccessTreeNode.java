package ru.msvdev.ds.server.security.access;

import lombok.Setter;
import ru.msvdev.ds.server.security.HttpRequest;
import ru.msvdev.ds.server.security.Permission;

@Setter
public class AnyPathAccessTreeNode extends RootAccessTreeNode {

    private int level;

    @Override
    public Permission getPermission(HttpRequest httpRequest) {
        String[] pathParts = httpRequest.pathParts();

        if (level < pathParts.length) {
            return super.getPermission(httpRequest);
        }

        return Permission.BAD_REQUEST;
    }
}
