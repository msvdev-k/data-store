package ru.msvdev.ds.server.security.access;

import lombok.Setter;
import ru.msvdev.ds.server.security.HttpRequest;
import ru.msvdev.ds.server.security.Permission;
import ru.msvdev.ds.server.security.UserAccessService;


@Setter
public class RootAccessTreeNode implements UserAccessService {

    protected UserAccessService[] accessTreeNodes;

    @Override
    public Permission getPermission(HttpRequest httpRequest) {
        for (UserAccessService node : accessTreeNodes) {
            Permission permission = node.getPermission(httpRequest);

            switch (permission) {
                case BAD_REQUEST -> {
                    continue;
                }
                case OK -> {
                    return Permission.OK;
                }
                case FORBIDDEN -> {
                    return Permission.FORBIDDEN;
                }
            }
        }

        return Permission.BAD_REQUEST;
    }
}
