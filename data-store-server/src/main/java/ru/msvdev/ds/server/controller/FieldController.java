package ru.msvdev.ds.server.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ru.msvdev.ds.server.service.FieldService;
import ru.msvdev.ds.server.openapi.api.FieldApi;
import ru.msvdev.ds.server.openapi.model.FieldRequest;
import ru.msvdev.ds.server.openapi.model.FieldResponse;

import java.util.List;
import java.util.UUID;


@RestController
@RequiredArgsConstructor
public class FieldController implements FieldApi {

    private final FieldService fieldService;


    @Override
    public ResponseEntity<FieldResponse> addField(UUID userUUID, Long catalogId, FieldRequest fieldRequest) {
        FieldResponse fieldResponse = fieldService.newField(catalogId, fieldRequest);
        return new ResponseEntity<>(fieldResponse, HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<List<FieldResponse>> fieldList(UUID userUUID, Long catalogId) {
        List<FieldResponse> fieldResponses = fieldService.getAllFields(catalogId);
        return new ResponseEntity<>(fieldResponses, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Void> removeFieldById(UUID userUUID, Long catalogId, Long fieldId) {
        fieldService.deleteField(catalogId, fieldId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Override
    public ResponseEntity<FieldResponse> updateFieldById(UUID userUUID, Long catalogId, Long fieldId, FieldRequest fieldRequest) {
        FieldResponse fieldResponse = fieldService.updateField(catalogId, fieldId, fieldRequest);
        return new ResponseEntity<>(fieldResponse, HttpStatus.OK);
    }
}
