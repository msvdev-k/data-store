package ru.msvdev.ds.server.module.catalog.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ru.msvdev.ds.server.module.catalog.card.service.CardService;
import ru.msvdev.ds.server.module.catalog.entity.Catalog;
import ru.msvdev.ds.server.module.catalog.mapper.CatalogResponseMapper;
import ru.msvdev.ds.server.module.catalog.repository.CatalogRepository;
import ru.msvdev.ds.server.openapi.model.CatalogRequest;
import ru.msvdev.ds.server.openapi.model.CatalogResponse;
import ru.msvdev.ds.server.security.Authority;

import java.util.*;


@Service
@RequiredArgsConstructor
public class CatalogService {

    private final CatalogRepository catalogRepository;
    private final CatalogResponseMapper catalogResponseMapper;
    private final CardService cardService;


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

        Catalog catalog = catalogRepository.insert(userUUID, name.trim(), description, Authority.MASTER);
        if (catalog == null) {
            throw new RuntimeException("Создать картотеку не удалось");
        }

        return catalogResponseMapper.convert(catalog);
    }


    @Transactional(readOnly = true)
    public List<CatalogResponse> getAllCatalogs(UUID userUUID) {
        return catalogRepository
                .findAll(userUUID)
                .stream()
                .map(catalogResponseMapper::convert)
                .toList();
    }


    @Transactional
    public void deleteCatalog(long catalogId) {
        int cardCount = cardService.count(catalogId);
        if (cardCount > 10) {
            throw new RuntimeException("Запрещено удалять картотеку если в ней хранится более 10 карточек!");
        }

        boolean result = catalogRepository.deleteById(catalogId);
        if (!result) {
            throw new RuntimeException("Удалить картотеку не удалось");
        }
    }


    @Transactional
    public CatalogResponse updateCatalog(UUID userUuid, long catalogId, CatalogRequest catalogRequest) {
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

        Catalog catalog = catalogRepository.findById(userUuid, catalogId);
        if (catalog == null) {
            throw new RuntimeException("Картотека не найдена");
        }

        return catalogResponseMapper.convert(catalog);
    }

}
