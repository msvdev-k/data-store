package ru.msvdev.ds.server.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.msvdev.ds.server.dao.entity.Tag;
import ru.msvdev.ds.server.dao.repository.CardRepository;
import ru.msvdev.ds.server.dao.repository.FieldRepository;
import ru.msvdev.ds.server.dao.repository.TagRepository;
import ru.msvdev.ds.server.service.value.ValueService;
import ru.msvdev.ds.server.openapi.model.CardTag;
import ru.msvdev.ds.server.utils.type.ValueType;

import java.util.List;
import java.util.Map;


@Service
@RequiredArgsConstructor
public class TagService {

    private final TagRepository tagRepository;
    private final CardRepository cardRepository;
    private final FieldRepository fieldRepository;

    private final FieldService fieldService;
    private final Map<String, ValueService> valueServiceMap;


    @Transactional(readOnly = true)
    public List<CardTag> getTags(Long catalogId, Long cardId) {
        if (!cardRepository.existsById(catalogId, cardId)) {
            throw new RuntimeException("Запрашиваемая карточка не существует");
        }

        return tagRepository
                .findAll(catalogId, cardId)
                .stream()
                .map(this::convert)
                .toList();
    }


    @Transactional
    public List<CardTag> addTags(Long catalogId, Long cardId, List<CardTag> cardTags) {
        if (!cardRepository.existsById(catalogId, cardId)) {
            throw new RuntimeException("Запрашиваемая карточка не существует");
        }

        for (CardTag tag : cardTags) {
            Long fieldId = tag.getFieldId();
            String value = tag.getValue();

            if (!fieldRepository.existsById(catalogId, fieldId)) {
                throw new RuntimeException("Запрашиваемого поля не существует");
            }

            ValueType valueType = fieldService.getValueType(catalogId, fieldId);
            ValueService valueService = valueServiceMap.get(valueType.serviceBeanName);

            if (valueService != null) {
                Long valueId = valueService.put(value);

                if (tagRepository.isExists(cardId, fieldId, valueId)) continue;

                if (!tagRepository.insert(cardId, fieldId, valueId)) {
                    throw new RuntimeException("Тег вставить не удалось");
                }
            }
        }

        return getTags(catalogId, cardId);
    }


    @Transactional
    public void deleteAllTags(Long catalogId, Long cardId) {
        tagRepository.delete(catalogId, cardId);
    }


    @Transactional
    public void deleteTag(Long catalogId, Long cardId, Long fieldId) {
        tagRepository.delete(catalogId, cardId, fieldId);
    }


    private CardTag convert(Tag tag) {
        ValueService valueService = valueServiceMap.get(tag.valueType().serviceBeanName);
        if (valueService == null) throw new RuntimeException("Структура данных карточки нарушена");

        String value = valueService.get(tag.valueId());

        CardTag cardTag = new CardTag();
        cardTag.setFieldId(tag.fieldId());
        cardTag.setValue(value);

        return cardTag;
    }

}
