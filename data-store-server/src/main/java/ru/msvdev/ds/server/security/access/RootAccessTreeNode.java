package ru.msvdev.ds.server.security.access;

import lombok.Setter;
import ru.msvdev.ds.server.security.HttpRequest;
import ru.msvdev.ds.server.security.Permission;
import ru.msvdev.ds.server.security.AccessService;


@Setter
public class RootAccessTreeNode implements AccessService {

    protected AccessService[] accessTreeNodes;

    @Override
    public Permission getPermission(HttpRequest httpRequest) {
        for (AccessService node : accessTreeNodes) {
            Permission permission = node.getPermission(httpRequest);

            if (permission != Permission.BAD_REQUEST)
                return permission;
        }

        return Permission.BAD_REQUEST;
    }
}
