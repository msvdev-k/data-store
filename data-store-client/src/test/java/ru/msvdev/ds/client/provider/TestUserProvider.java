package ru.msvdev.ds.client.provider;

import ru.msvdev.ds.client.model.user.User;

import java.util.UUID;

/**
 * Провайдер пользователей для проведения тестов
 */
public class TestUserProvider extends UserProvider {

    private final User authUser;

    public TestUserProvider(User authUser) {
        this.authUser = authUser;
    }


    public static TestUserProvider getInstance() {
        return getInstance(
                User.builder().uuid(UUID.randomUUID()).build()
        );
    }

    public static TestUserProvider getInstance(User user) {
        return new TestUserProvider(user);
    }


    @Override
    public User getAuthUser() {
        return authUser;
    }
}
