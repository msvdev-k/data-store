package ru.msvdev.ds.server.module.card.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.msvdev.ds.server.module.card.entity.Card;
import ru.msvdev.ds.server.module.card.repository.CardRepository;
import ru.msvdev.ds.server.openapi.model.CardResponse;
import ru.msvdev.ds.server.openapi.model.CardTag;
import ru.msvdev.ds.server.module.tag.service.TagService;

import java.util.ArrayList;
import java.util.List;


@Service
@RequiredArgsConstructor
public class CardService {

    private final CardRepository cardRepository;
    private final TagService tagService;


    @Transactional
    public CardResponse newCard(long catalogId, List<CardTag> cardTags) {
        Card card = cardRepository.insert(catalogId);
        List<CardTag> cardTagList = tagService.addTags(catalogId, card.id(), cardTags);
        CardResponse cardResponse = new CardResponse();
        cardResponse.setId(card.id());
        cardResponse.setTags(cardTagList);
        return cardResponse;
    }


    @Transactional(readOnly = true)
    public List<CardResponse> getAllCards(long catalogId) {
        List<Card> cards = cardRepository.getCards(catalogId);
        List<CardResponse> cardResponses = new ArrayList<>(cards.size());

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
    public CardResponse getCardById(long catalogId, long cardId) {
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
    public void deleteCard(long catalogId, long cardId) {
        cardRepository.deleteById(catalogId, cardId);
    }


    @Transactional(readOnly = true)
    public int count(long catalogId) {
        return cardRepository.count(catalogId);
    }

    @Transactional(readOnly = true)
    public boolean existsById(long catalogId, long cardId) {
        return cardRepository.existsById(catalogId, cardId);
    }

}
