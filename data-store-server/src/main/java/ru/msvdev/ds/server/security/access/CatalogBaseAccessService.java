package ru.msvdev.ds.server.security.access;

import lombok.Setter;
import org.springframework.http.HttpMethod;
import ru.msvdev.ds.server.security.*;

import java.util.*;


public class CatalogBaseAccessService implements UserAccessService {

    private UserAccessService accessRootNode;

    private CatalogBaseAccessService() {
    }

    public static CatalogBaseAccessServiceBuilder builder() {
        return new CatalogBaseAccessServiceBuilder();
    }

    @Override
    public Permission getPermission(HttpRequest httpRequest) {
        return accessRootNode.getPermission(httpRequest);
    }


    /**
     * Построитель, помогающий настроить работу сервиса {@link UserAccessService}
     */
    public static class CatalogBaseAccessServiceBuilder {

        private CatalogBaseAccessServiceBuilder() {
        }

        private final PathNode rootNode;

        {
            rootNode = new PathNode();
            rootNode.setLevel(-1);
        }

        public CatalogBaseAccessServiceBuilder setAuthorityService(UserAuthorityService userAuthorityService) {
            rootNode.setUserAuthorityService(userAuthorityService);
            return this;
        }

        public CatalogBaseAccessServiceBuilder addMatcher(String path, HttpMethod method, Authority... authority) {
            String[] pathParts = path.split("/");
            rootNode.addPath(pathParts, method, authority);
            return this;
        }

        public CatalogBaseAccessService build() {
            CatalogBaseAccessService service = new CatalogBaseAccessService();
            service.accessRootNode = rootNode.buildAccessServiceTree();
            return service;
        }


        /**
         * Узел вспомогательного дерева, необходимого для построения дерева путей доступа к ресурсам
         */
        private static class PathNode {

            private static final String CATALOG_ID_PATH = "{catalogId}";
            private static final String ALL_MATCH_PATH = "*";

            @Setter
            private int level;
            @Setter
            private UserAuthorityService userAuthorityService;

            private final Map<HttpMethod, Set<Authority>> methods;
            private final Map<String, PathNode> childNodes;

            {
                methods = new HashMap<>();
                childNodes = new HashMap<>();
            }

            public void addPath(String[] pathParts, HttpMethod method, Authority[] authorities) {
                int nextLevel = level + 1;

                if (nextLevel < pathParts.length) {
                    String part = pathParts[nextLevel];

                    if (childNodes.containsKey(part)) {
                        childNodes.get(part).addPath(pathParts, method, authorities);

                    } else {
                        PathNode pathNode = new PathNode();
                        pathNode.setLevel(nextLevel);
                        pathNode.setUserAuthorityService(userAuthorityService);
                        childNodes.put(part, pathNode);
                        pathNode.addPath(pathParts, method, authorities);
                    }

                } else {
                    methods.put(method, Set.of(authorities));
                }
            }


            public UserAccessService buildAccessServiceTree() {
                UserAccessService[] accessTreeNodes = new UserAccessService[childNodes.size() + methods.size()];
                int i = 0;

                for (HttpMethod method : methods.keySet()) {
                    accessTreeNodes[i] = buildMethodNode(method);
                    i++;
                }

                for (String path : childNodes.keySet()) {
                    accessTreeNodes[i] = buildPathNode(path);
                    i++;
                }

                RootAccessTreeNode rootAccessTreeNode = new RootAccessTreeNode();
                rootAccessTreeNode.setAccessTreeNodes(accessTreeNodes);
                return rootAccessTreeNode;
            }


            private UserAccessService buildMethodNode(HttpMethod httpMethod) {
                MethodAccessTreeNode accessTreeNode = new MethodAccessTreeNode();
                accessTreeNode.setHttpMethod(httpMethod);
                accessTreeNode.setAuthorities(methods.get(httpMethod));
                return accessTreeNode;
            }

            private UserAccessService buildPathNode(String path) {
                PathNode pathNode = this.childNodes.get(path);

                Map<HttpMethod, Set<Authority>> methods = pathNode.methods;
                Map<String, PathNode> childNodes = pathNode.childNodes;

                UserAccessService[] accessTreeNodes = new UserAccessService[childNodes.size() + methods.size()];
                int i = 0;

                for (HttpMethod method : methods.keySet()) {
                    accessTreeNodes[i] = pathNode.buildMethodNode(method);
                    i++;
                }

                for (String childPath : childNodes.keySet()) {
                    accessTreeNodes[i] = pathNode.buildPathNode(childPath);
                    i++;
                }

                if (path.equals(CATALOG_ID_PATH)) {
                    CatalogIdAccessTreeNode node = new CatalogIdAccessTreeNode();
                    node.setLevel(pathNode.level);
                    node.setUserAuthorityService(pathNode.userAuthorityService);
                    node.setAccessTreeNodes(accessTreeNodes);
                    return node;
                }

                if (path.equals(ALL_MATCH_PATH)) {
                    AnyPathAccessTreeNode node = new AnyPathAccessTreeNode();
                    node.setLevel(pathNode.level);
                    node.setAccessTreeNodes(accessTreeNodes);
                    return node;
                }

                PathAccessTreeNode node = new PathAccessTreeNode();
                node.setLevel(pathNode.level);
                node.setPath(path);
                node.setAccessTreeNodes(accessTreeNodes);
                return node;
            }
        }
    }
}
