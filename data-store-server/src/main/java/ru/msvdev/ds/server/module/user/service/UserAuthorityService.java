package ru.msvdev.ds.server.module.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.msvdev.ds.server.module.user.repository.UserAuthorityRepository;
import ru.msvdev.ds.server.openapi.model.CatalogAuthority;
import ru.msvdev.ds.server.openapi.model.UserAuthorities;
import ru.msvdev.ds.server.security.Authority;
import ru.msvdev.ds.server.security.AuthorityService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class UserAuthorityService implements AuthorityService {

    private final UserAuthorityRepository userAuthorityRepository;


    @Transactional(readOnly = true)
    public List<UserAuthorities> getAllUsers(Long catalogId) {
        List<UUID> users = userAuthorityRepository.findAllUsers(catalogId);
        List<UserAuthorities> userAuthoritiesList = new ArrayList<>();

        for (UUID userUuid : users) {
            List<CatalogAuthority> catalogAuthorities = userAuthorityRepository
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

        List<Authority> newAuthorities = userAuthorities
                .getAuthorities()
                .stream()
                .map(catalogAuthority -> Authority.valueOf(catalogAuthority.name()))
                .distinct()
                .toList();

        List<Authority> oldAuthorities = userAuthorityRepository.findAllAuthorities(catalogId, userUuid);

        List<Authority> removeAuthorities = oldAuthorities.stream()
                .filter(oldAuthority -> oldAuthority != Authority.MASTER && !newAuthorities.contains(oldAuthority))
                .toList();

        List<Authority> addAuthorities = newAuthorities.stream()
                .filter(newAuthority -> newAuthority != Authority.MASTER && !oldAuthorities.contains(newAuthority))
                .toList();

        for (Authority authority : removeAuthorities) {
            if (!userAuthorityRepository.removeAuthority(catalogId, userUuid, authority)) {
                throw new RuntimeException("Не удалось обновить полномочия пользователя!");
            }
        }

        for (Authority authority : addAuthorities) {
            if (!userAuthorityRepository.addAuthority(catalogId, userUuid, authority)) {
                throw new RuntimeException("Не удалось обновить полномочия пользователя!");
            }
        }

        List<CatalogAuthority> updatedAuthorities = userAuthorityRepository
                .findAllAuthorities(catalogId, userUuid)
                .stream()
                .map(authorityType -> CatalogAuthority.valueOf(authorityType.name()))
                .toList();

        return new UserAuthorities(userUuid, updatedAuthorities);
    }


    @Transactional
    public void deleteUser(Long catalogId, UUID user) {
        List<Authority> allAuthorities = userAuthorityRepository.findAllAuthorities(catalogId, user);
        if (allAuthorities.contains(Authority.MASTER)) {
            throw new RuntimeException("Пользователя с полномочиями MASTER удалить не возможно");
        }
        userAuthorityRepository.removeAllAuthorities(catalogId, user);
    }


    @Override
    @Transactional(readOnly = true)
    public List<Authority> loadAuthorities(UUID userUuid, long catalogId) {
        return userAuthorityRepository.findAllAuthorities(catalogId, userUuid);
    }
}
