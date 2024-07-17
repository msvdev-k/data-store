package ru.msvdev.ds.server.security.access;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpMethod;
import ru.msvdev.ds.server.security.*;

import java.util.*;


@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CatalogBasedAccessService implements AccessService {

    private AccessService accessRootNode;

    @Override
    public Permission getPermission(HttpRequest httpRequest) {
        return accessRootNode.getPermission(httpRequest);
    }


    public static CatalogBaseAccessServiceBuilder builder() {
        return new CatalogBaseAccessServiceBuilder();
    }


    /**
     * Построитель, помогающий настроить работу сервиса {@link AccessService}
     */
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class CatalogBaseAccessServiceBuilder {

        private final AccessNodeBuilder rootNode = new AccessNodeBuilder();

        {
            rootNode.setLevel(0);
        }

        public CatalogBaseAccessServiceBuilder setAuthorityService(AuthorityService authorityService) {
            rootNode.setAuthorityService(authorityService);
            return this;
        }

        public CatalogBaseAccessServiceBuilder addMatcher(String path, HttpMethod method, Authority... authority) {
            String[] pathParts = path.split("/");
            rootNode.addPath(pathParts, method, authority);
            return this;
        }

        public CatalogBasedAccessService build() {
            CatalogBasedAccessService service = new CatalogBasedAccessService();
            service.accessRootNode = rootNode.buildAccessServiceTree();
            return service;
        }


        /**
         * Узел вспомогательного дерева, необходимого для построения дерева путей доступа к ресурсам
         */
        private static class AccessNodeBuilder {

            private static final String CATALOG_ID_PATH = "{catalogId}";
            private static final String ALL_MATCH_PATH = "*";

            @Setter
            private int level;
            @Setter
            private AuthorityService authorityService;

            private final Map<HttpMethod, Set<Authority>> methods = new HashMap<>();
            private final Map<String, AccessNodeBuilder> childNodes = new HashMap<>();


            public void addPath(String[] pathParts, HttpMethod method, Authority[] authorities) {
                if (level < pathParts.length) {
                    String part = pathParts[level];

                    if (childNodes.containsKey(part)) {
                        childNodes.get(part).addPath(pathParts, method, authorities);

                    } else {
                        AccessNodeBuilder accessNodeBuilder = new AccessNodeBuilder();
                        accessNodeBuilder.setLevel(level + 1);
                        accessNodeBuilder.setAuthorityService(authorityService);
                        accessNodeBuilder.addPath(pathParts, method, authorities);
                        childNodes.put(part, accessNodeBuilder);
                    }

                } else {
                    methods.put(method, Set.of(authorities));
                }
            }


            public AccessService buildAccessServiceTree() {
                AccessService[] accessTreeNodes = new AccessService[childNodes.size() + methods.size()];
                int i = 0;

                for (String path : childNodes.keySet()) {
                    accessTreeNodes[i] = buildPathNode(path);
                    i++;
                }

                for (HttpMethod method : methods.keySet()) {
                    accessTreeNodes[i] = buildMethodNode(method);
                    i++;
                }

                RootAccessTreeNode rootAccessTreeNode = new RootAccessTreeNode();
                rootAccessTreeNode.setAccessTreeNodes(accessTreeNodes);
                return rootAccessTreeNode;
            }


            private AccessService buildMethodNode(HttpMethod httpMethod) {
                MethodAccessTreeNode accessTreeNode = new MethodAccessTreeNode();
                accessTreeNode.setLevel(level + 1);
                accessTreeNode.setHttpMethod(httpMethod);
                accessTreeNode.setAuthorities(methods.get(httpMethod));
                return accessTreeNode;
            }


            private AccessService buildPathNode(String path) {
                AccessNodeBuilder accessNodeBuilder = this.childNodes.get(path);

                Map<HttpMethod, Set<Authority>> methods = accessNodeBuilder.methods;
                Map<String, AccessNodeBuilder> childNodes = accessNodeBuilder.childNodes;

                AccessService[] accessTreeNodes = new AccessService[childNodes.size() + methods.size()];
                int i = 0;

                for (String childPath : childNodes.keySet()) {
                    accessTreeNodes[i] = accessNodeBuilder.buildPathNode(childPath);
                    i++;
                }

                for (HttpMethod method : methods.keySet()) {
                    accessTreeNodes[i] = accessNodeBuilder.buildMethodNode(method);
                    i++;
                }


                if (path.equals(CATALOG_ID_PATH)) {
                    CatalogIdAccessTreeNode node = new CatalogIdAccessTreeNode();
                    node.setLevel(accessNodeBuilder.level);
                    node.setAuthorityService(accessNodeBuilder.authorityService);
                    node.setAccessTreeNodes(accessTreeNodes);
                    return node;
                }

                if (path.equals(ALL_MATCH_PATH)) {
                    AnyPathAccessTreeNode node = new AnyPathAccessTreeNode();
                    node.setLevel(accessNodeBuilder.level);
                    node.setAccessTreeNodes(accessTreeNodes);
                    return node;
                }

                PathAccessTreeNode node = new PathAccessTreeNode();
                node.setLevel(accessNodeBuilder.level);
                node.setPath(path);
                node.setAccessTreeNodes(accessTreeNodes);
                return node;
            }
        }
    }
}
