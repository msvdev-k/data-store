package ru.msvdev.ds.server.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.msvdev.ds.server.dao.entity.Card;
import ru.msvdev.ds.server.dao.repository.CardRepository;
import ru.msvdev.ds.server.openapi.model.CardResponse;
import ru.msvdev.ds.server.openapi.model.CardTag;

import java.util.ArrayList;
import java.util.List;


@Service
@RequiredArgsConstructor
public class CardService {

    private final CardRepository cardRepository;
    private final TagService tagService;


    @Transactional
    public CardResponse newCard(Long catalogId, List<CardTag> cardTags) {
        Card card = cardRepository.insert(catalogId);
        List<CardTag> cardTagList = tagService.addTags(catalogId, card.id(), cardTags);
        CardResponse cardResponse = new CardResponse();
        cardResponse.setId(card.id());
        cardResponse.setTags(cardTagList);
        return cardResponse;
    }


    @Transactional(readOnly = true)
    public List<CardResponse> getAllCards(Long catalogId, Integer page, Integer size) {
        List<CardResponse> cardResponses = new ArrayList<>();
        List<Card> cards = cardRepository.getCards(catalogId);

        for (Card card : cards) {
            List<CardTag> cardTagList = tagService.getTags(catalogId, card.id());

            CardResponse cardResponse = new CardResponse();
            cardResponse.setId(card.id());
            cardResponse.setTags(cardTagList);

            cardResponses.add(cardResponse);
        }

        return cardResponses;
    }


    @Transactional(readOnly = true)
    public CardResponse getCardById(Long catalogId, Long cardId) {
        if (!cardRepository.existsById(catalogId, cardId)) {
            throw new RuntimeException("Карточка не найдена");
        }

        List<CardTag> cardTagList = tagService.getTags(catalogId, cardId);

        CardResponse cardResponse = new CardResponse();
        cardResponse.setId(cardId);
        cardResponse.setTags(cardTagList);

        return cardResponse;
    }


    @Transactional
    public void deleteCard(Long catalogId, Long cardId) {
        cardRepository.deleteById(catalogId, cardId);
    }


}
