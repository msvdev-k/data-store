package ru.msvdev.ds.client.provider;

import ru.msvdev.ds.client.openapi.ApiClient;
import ru.msvdev.ds.client.model.user.User;


/**
 * Провайдер Data Store для проведения тестов
 */
public class TestDataStoreProvider extends DataStoreProvider {

    private final UserProvider userProvider;
    private final User authUser;

    private TestDataStoreProvider(UserProvider userProvider) {
        this.userProvider = userProvider;
        this.authUser = userProvider.getAuthUser();
    }


    public static TestDataStoreProvider getInstance(ProviderConfiguration configuration) {
        return getInstance(configuration, TestUserProvider.getInstance());
    }

    public static TestDataStoreProvider getInstance(ProviderConfiguration configuration, UserProvider userProvider) {
        TestDataStoreProvider provider = new TestDataStoreProvider(userProvider);

        ApiClient client = provider.getApiClient();
        client.setScheme(configuration.getScheme());
        client.setHost(configuration.getHost());
        client.setPort(configuration.getPort());

        return provider;
    }


    @Override
    public User getAuthUser() {
        return authUser;
    }

    @Override
    public UserProvider getUserProvider() {
        return userProvider;
    }
}
