package ru.msvdev.ds.server.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ru.msvdev.ds.server.service.CatalogService;
import ru.msvdev.ds.server.openapi.api.CatalogApi;
import ru.msvdev.ds.server.openapi.model.CatalogRequest;
import ru.msvdev.ds.server.openapi.model.CatalogResponse;

import java.util.List;
import java.util.UUID;


@RestController
@RequiredArgsConstructor
public class CatalogController implements CatalogApi {

    private final CatalogService catalogService;

    @Override
    public ResponseEntity<CatalogResponse> addCatalog(UUID userUUID, CatalogRequest catalogRequest) {
        CatalogResponse catalogResponse = catalogService.newCatalog(userUUID, catalogRequest);
        return new ResponseEntity<>(catalogResponse, HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<List<CatalogResponse>> catalogList(UUID userUUID) {
        List<CatalogResponse> catalogResponses = catalogService.getAllCatalogs(userUUID);
        return new ResponseEntity<>(catalogResponses, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Void> removeCatalogById(UUID userUUID, Long catalogId) {
        catalogService.deleteCatalog(catalogId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Override
    public ResponseEntity<CatalogResponse> updateCatalogById(UUID userUUID, Long catalogId, CatalogRequest catalogRequest) {
        CatalogResponse catalogResponse = catalogService.updateCatalog(catalogId, catalogRequest);
        return new ResponseEntity<>(catalogResponse, HttpStatus.OK);
    }
}
