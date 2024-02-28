package ru.msvdev.ds.server.data.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ru.msvdev.ds.server.data.entity.Card;
import ru.msvdev.ds.server.data.entity.Catalog;
import ru.msvdev.ds.server.data.repository.CardRepository;
import ru.msvdev.ds.server.data.repository.CatalogRepository;
import ru.msvdev.ds.server.openapi.model.CatalogAuthority;
import ru.msvdev.ds.server.openapi.model.CatalogRequest;
import ru.msvdev.ds.server.openapi.model.CatalogResponse;
import ru.msvdev.ds.server.security.Authority;

import java.util.*;


@Service
@RequiredArgsConstructor
public class CatalogService {

    private final CatalogRepository catalogRepository;
    private final CardRepository cardRepository;

    @Transactional
    public CatalogResponse newCatalog(UUID userUUID, CatalogRequest catalogRequest) {
        String name = catalogRequest.getName();
        String description = catalogRequest.getDescription();

        if (name == null || name.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Название картотеки не должно быть пустым!");
        }

        if (description != null && description.isBlank()) {
            description = null;
        }

        Catalog catalog = catalogRepository.insert(name.trim(), description);
        boolean result = catalogRepository.addAuthority(catalog.id(), userUUID, Authority.MASTER);

        if (!result) {
            throw new RuntimeException("Создать картотеку не удалось");
        }

        CatalogResponse catalogResponse = convert(catalog);
        catalogResponse.getAuthorities().add(CatalogAuthority.MASTER);

        return catalogResponse;
    }


    @Transactional(readOnly = true)
    public List<CatalogResponse> getAllCatalogs(UUID userUUID) {
        return catalogRepository
                .findAll(userUUID)
                .stream()
                .map(this::convert)
                .toList();
    }


    @Transactional
    public void deleteCatalog(Long catalogId) {
        int cardCount = cardRepository.count(catalogId);
        if (cardCount > 10) {
            throw new RuntimeException("Запрещено удалять картотеку если в ней хранится более 10 карточек!");
        }

        List<Card> cards = cardRepository.getCards(catalogId);
        for (Card card : cards) {
            boolean result = cardRepository.deleteById(catalogId, card.id());
            if (!result) {
                throw new RuntimeException("Удалить карточку из картотеки не удалось");
            }
        }

        boolean result = catalogRepository.deleteById(catalogId);
        if (!result) {
            throw new RuntimeException("Удалить картотеку не удалось");
        }
    }


    @Transactional
    public CatalogResponse updateCatalog(Long catalogId, CatalogRequest catalogRequest) {
        String name = catalogRequest.getName();
        String description = catalogRequest.getDescription();

        if (name != null && !name.isBlank()) {
            boolean result = catalogRepository.updateName(catalogId, name.trim());
            if (!result) {
                throw new RuntimeException("Ошибка переименования картотеки");
            }
        }

        if (description != null) {
            if (description.isBlank()) {
                description = null;
            } else {
                description = description.trim();
            }
            boolean result = catalogRepository.updateDescription(catalogId, description);
            if (!result) {
                throw new RuntimeException("Ошибка изменения описания картотеки");
            }
        }

        Optional<Catalog> optionalCatalog = catalogRepository.findById(catalogId);

        if (optionalCatalog.isEmpty()) {
            throw new RuntimeException("Картотека не найдена");
        }

        return convert(optionalCatalog.get());
    }


    private CatalogResponse convert(Catalog catalog) {
        CatalogResponse catalogResponse = new CatalogResponse();
        catalogResponse.setId(catalog.id());
        catalogResponse.setName(catalog.name());
        catalogResponse.setDescription(catalog.description());

        if (catalog.authorities() != null) {
            catalogResponse.setAuthorities(
                    Arrays.stream(catalog.authorities())
                            .map(Authority::name)
                            .map(CatalogAuthority::valueOf)
                            .toList()
            );

        } else {
            catalogResponse.setAuthorities(new ArrayList<>());
        }

        return catalogResponse;
    }
}
