package ru.msvdev.ds.server.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import ru.msvdev.ds.server.service.TagService;
import ru.msvdev.ds.server.openapi.api.TagApi;
import ru.msvdev.ds.server.openapi.model.CardResponse;
import ru.msvdev.ds.server.openapi.model.CardTag;

import java.util.List;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class TagController implements TagApi {

    private final TagService tagService;


    @Override
    public ResponseEntity<CardResponse> addTags(UUID userUUID, Long catalogId, Long cardId, List<CardTag> cardTags) {
        List<CardTag> cardTagList = tagService.addTags(catalogId, cardId, cardTags);
        CardResponse cardResponse = new CardResponse();
        cardResponse.setId(cardId);
        cardResponse.setTags(cardTagList);
        return new ResponseEntity<>(cardResponse, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Void> removeAllTags(UUID userUUID, Long catalogId, Long cardId) {
        tagService.deleteAllTags(catalogId, cardId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Override
    public ResponseEntity<Void> removeTag(UUID userUUID, Long catalogId, Long cardId, Long fieldId) {
        tagService.deleteTag(catalogId, cardId, fieldId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
