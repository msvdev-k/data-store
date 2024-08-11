package ru.msvdev.ds.client.provider;

import ru.msvdev.ds.client.openapi.ApiClient;

import java.util.UUID;


/**
 * Провайдер Data Store для проведения тестов
 */
public class TestDataStoreProvider extends DataStoreProvider {

    private final UUID userUuid;

    private TestDataStoreProvider(ApiClient apiClient, UUID userUuid) {
        super(apiClient);
        this.userUuid = userUuid;
    }


    public static TestDataStoreProvider getInstance(String host, int port) {
        return getInstance(host, port, UUID.randomUUID());
    }

    public static TestDataStoreProvider getInstance(String host, int port, UUID userUuid) {
        ApiClient client = new ApiClient() {
            @Override
            protected String getDefaultBaseUri() {
                return "http://localhost";
            }
        };
        client.setScheme("http");
        client.setHost(host);
        client.setPort(port);

        return new TestDataStoreProvider(client, userUuid);
    }


    @Override
    public UUID getUserUuid() {
        return userUuid;
    }
}
