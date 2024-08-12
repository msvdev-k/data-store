package ru.msvdev.ds.client.base;

import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import org.testcontainers.DockerClientFactory;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.DockerImageName;
import ru.msvdev.ds.client.provider.ProviderConfiguration;


/**
 * Статический класс поднимающий Docker контейнеры для проведения тестов взаимодействия с сервером Data Store
 */
public class DataStoreContainerTest {

    private static final String REUSE_UUID = "726fa4d1-8919-4566-afa4-d1891955668f";
    private static final String NETWORK_NAME = "DataStoreContainerTest-" + REUSE_UUID;
    private static final String POSTGRESQL_ALIAS = "PostgreSQL-Alias-" + REUSE_UUID;

    private static final DockerImageName POSTGRESQL_IMAGE = DockerImageName.parse("postgres:16.3-alpine");
    private static final DockerImageName DATA_STORE_IMAGE = DockerImageName.parse("msvdevk/data-store-server:0.2-openjdk-17-alpine");

    private static final PostgreSQLContainer<?> POSTGRESQL_CONTAINER = new PostgreSQLContainer<>(POSTGRESQL_IMAGE);
    private static final GenericContainer<?> DATA_STORE_CONTAINER = new GenericContainer<>(DATA_STORE_IMAGE);

    static {
        System.out.println("Start test containers...");

        POSTGRESQL_CONTAINER
                .withReuse(true)
                .withLabel("reuse.UUID", REUSE_UUID)

                .withNetwork(reusableNetwork())
                .withNetworkAliases(POSTGRESQL_ALIAS)

                .waitingFor(Wait.forSuccessfulCommand("pg_isready -q -d $${POSTGRES_DB} -U $${POSTGRES_USER}"))

                .start();


        DATA_STORE_CONTAINER
                .dependsOn(POSTGRESQL_CONTAINER)
                .withNetwork(POSTGRESQL_CONTAINER.getNetwork())

                .withReuse(true)
                .withLabel("reuse.UUID", REUSE_UUID)

                .withExposedPorts(8980)

                .withEnv("POSTGRES_HOST", POSTGRESQL_ALIAS)
                .withEnv("POSTGRES_DB", POSTGRESQL_CONTAINER.getDatabaseName())
                .withEnv("POSTGRES_USER", POSTGRESQL_CONTAINER.getUsername())
                .withEnv("POSTGRES_PASSWORD", POSTGRESQL_CONTAINER.getPassword())

                .start();

        System.out.println("Containers started!");
    }


    public static String dataStoreHost() {
        return DATA_STORE_CONTAINER.getHost();
    }

    public static int dataStorePort() {
        return DATA_STORE_CONTAINER.getFirstMappedPort();
    }

    public static String dataStoreUrl() {
        return String.format("http://%s:%d", DATA_STORE_CONTAINER.getHost(), DATA_STORE_CONTAINER.getFirstMappedPort());
    }

    public static ProviderConfiguration providerConfiguration() {
        return ProviderConfiguration.builder()
                .scheme("http")
                .host(DATA_STORE_CONTAINER.getHost())
                .port(DATA_STORE_CONTAINER.getFirstMappedPort())
                .build();
    }

    private static Network reusableNetwork() {
        final String id = DockerClientFactory.instance().client()
                .listNetworksCmd()
                .exec()
                .stream()
                .filter(network -> network.getName().equals(NETWORK_NAME))
                .filter(network -> network.getLabels().equals(DockerClientFactory.DEFAULT_LABELS))
                .map(com.github.dockerjava.api.model.Network::getId)
                .findFirst()
                .orElseGet(
                        () -> DockerClientFactory.instance().client()
                                .createNetworkCmd()
                                .withName(NETWORK_NAME)
                                .withCheckDuplicate(true)
                                .withLabels(DockerClientFactory.DEFAULT_LABELS)
                                .exec()
                                .getId()
                );

        return new Network() {
            @Override
            public Statement apply(Statement base, Description description) {
                return base;
            }

            @Override
            public String getId() {
                return id;
            }

            @Override
            public void close() {
                // never close
            }
        };
    }
}
