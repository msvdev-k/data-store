package ru.msvdev.ds.server.module.tag.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import ru.msvdev.ds.server.module.card.service.CardService;
import ru.msvdev.ds.server.module.tag.service.TagService;
import ru.msvdev.ds.server.openapi.api.TagApi;
import ru.msvdev.ds.server.openapi.model.CardResponse;
import ru.msvdev.ds.server.openapi.model.CardTag;

import java.util.List;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class TagController implements TagApi {

    private final CardService cardService;
    private final TagService tagService;


    @Override
    public ResponseEntity<CardResponse> addTags(UUID userUUID, Long catalogId, Long cardId, List<CardTag> cardTags) {
        if (!cardService.existsById(catalogId, cardId)) {
            throw new RuntimeException("Запрашиваемая карточка не существует");
        }

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
