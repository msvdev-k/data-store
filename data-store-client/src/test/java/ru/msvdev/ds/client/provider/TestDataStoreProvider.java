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


    public static TestDataStoreProvider getInstance(ProviderConfiguration configuration) {
        return getInstance(configuration, UUID.randomUUID());
    }

    public static TestDataStoreProvider getInstance(ProviderConfiguration configuration, UUID userUuid) {
        TestDataStoreProvider provider = new TestDataStoreProvider(userUuid);

        ApiClient client = provider.getApiClient();
        client.setScheme(configuration.getScheme());
        client.setHost(configuration.getHost());
        client.setPort(configuration.getPort());

        return provider;
    }


    @Override
    public UUID getUserUuid() {
        return userUuid;
    }
}
