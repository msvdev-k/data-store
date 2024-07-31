package ru.msvdev.ds.server.module.card.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ru.msvdev.ds.server.openapi.api.CardApi;
import ru.msvdev.ds.server.openapi.model.CardResponse;
import ru.msvdev.ds.server.openapi.model.CardTag;
import ru.msvdev.ds.server.module.card.service.CardService;

import java.util.List;
import java.util.UUID;


@RestController
@RequiredArgsConstructor
public class CardController implements CardApi {

    private final CardService cardService;

    @Override
    public ResponseEntity<CardResponse> getCard(UUID userUUID, Long catalogId, Long cardId) {
        CardResponse cardResponse = cardService.getCardById(catalogId, cardId);
        return new ResponseEntity<>(cardResponse, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<CardResponse> addCard(UUID userUUID, Long catalogId, List<CardTag> cardTags) {
        CardResponse cardResponse = cardService.newCard(catalogId, cardTags);
        return new ResponseEntity<>(cardResponse, HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<List<CardResponse>> cardList(UUID userUUID, Long catalogId, Integer page, Integer size) {
        List<CardResponse> cardResponses = cardService.getAllCards(catalogId);
        return new ResponseEntity<>(cardResponses, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Void> removeCardById(UUID userUUID, Long catalogId, Long cardId) {
        cardService.deleteCard(catalogId, cardId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
