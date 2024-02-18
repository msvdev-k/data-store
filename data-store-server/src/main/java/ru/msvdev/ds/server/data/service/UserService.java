package ru.msvdev.ds.server.data.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.msvdev.ds.server.data.repository.CatalogRepository;
import ru.msvdev.ds.server.openapi.model.CatalogAuthority;
import ru.msvdev.ds.server.openapi.model.UserAuthorities;
import ru.msvdev.ds.server.sequrity.AuthorityType;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class UserService {

    private final CatalogRepository catalogRepository;


    @Transactional(readOnly = true)
    public List<UserAuthorities> getAllUsers(Long catalogId) {
        List<UUID> users = catalogRepository.findAllUsers(catalogId);
        List<UserAuthorities> userAuthoritiesList = new ArrayList<>();

        for (UUID userUuid : users) {
            List<CatalogAuthority> catalogAuthorities = catalogRepository
                    .findAllAuthorities(catalogId, userUuid)
                    .stream()
                    .map(authorityType -> CatalogAuthority.valueOf(authorityType.name()))
                    .toList();

            userAuthoritiesList.add(
                    new UserAuthorities(userUuid, catalogAuthorities)
            );
        }

        return userAuthoritiesList;
    }


    @Transactional
    public UserAuthorities updateAuthorities(Long catalogId, UserAuthorities userAuthorities) {
        UUID userUuid = userAuthorities.getUserUuid();

        List<AuthorityType> newAuthorities = userAuthorities
                .getAuthorities()
                .stream()
                .map(catalogAuthority -> AuthorityType.valueOf(catalogAuthority.name()))
                .distinct()
                .toList();

        List<AuthorityType> oldAuthorities = catalogRepository.findAllAuthorities(catalogId, userUuid);

        List<AuthorityType> removeAuthorities = oldAuthorities.stream()
                .filter(oldAuthority -> oldAuthority != AuthorityType.MASTER && !newAuthorities.contains(oldAuthority))
                .toList();

        List<AuthorityType> addAuthorities = newAuthorities.stream()
                .filter(newAuthority -> newAuthority != AuthorityType.MASTER && !oldAuthorities.contains(newAuthority))
                .toList();

        for (AuthorityType authority : removeAuthorities) {
            if (!catalogRepository.removeAuthority(catalogId, userUuid, authority)) {
                throw new RuntimeException("Не удалось обновить полномочия пользователя!");
            }
        }

        for (AuthorityType authority : addAuthorities) {
            if (!catalogRepository.addAuthority(catalogId, userUuid, authority)) {
                throw new RuntimeException("Не удалось обновить полномочия пользователя!");
            }
        }

        List<CatalogAuthority> updatedAuthorities = catalogRepository
                .findAllAuthorities(catalogId, userUuid)
                .stream()
                .map(authorityType -> CatalogAuthority.valueOf(authorityType.name()))
                .toList();

        return new UserAuthorities(userUuid, updatedAuthorities);
    }


    @Transactional
    public void deleteUser(Long catalogId, UUID user) {
        List<AuthorityType> allAuthorities = catalogRepository.findAllAuthorities(catalogId, user);
        if (allAuthorities.contains(AuthorityType.MASTER)) {
            throw new RuntimeException("Пользователя с полномочиями MASTER удалить не возможно");
        }
        catalogRepository.removeAllAuthorities(catalogId, user);
    }

}
