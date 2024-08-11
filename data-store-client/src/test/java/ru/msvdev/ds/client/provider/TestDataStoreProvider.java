package ru.msvdev.ds.client.provider;

import ru.msvdev.ds.client.openapi.ApiClient;

import java.util.UUID;


/**
 * Провайдер Data Store для проведения тестов
 */
public class TestDataStoreProvider extends DataStoreProvider {

    private final UUID userUuid;

    private TestDataStoreProvider(UUID userUuid) {
        this.userUuid = userUuid;
    }


    public static TestDataStoreProvider getInstance(String host, int port) {
        return getInstance(host, port, UUID.randomUUID());
    }

    public static TestDataStoreProvider getInstance(String host, int port, UUID userUuid) {
        TestDataStoreProvider provider = new TestDataStoreProvider(userUuid);

        ApiClient client = provider.getApiClient();
        client.setScheme("http");
        client.setHost(host);
        client.setPort(port);

        return provider;
    }


    @Override
    public UUID getUserUuid() {
        return userUuid;
    }
}
