package ru.msvdev.ds.server.module.tag.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.msvdev.ds.server.module.tag.mapper.TagResponseMapper;
import ru.msvdev.ds.server.module.tag.repository.TagRepository;
import ru.msvdev.ds.server.module.field.service.FieldService;
import ru.msvdev.ds.server.module.value.service.DataService;
import ru.msvdev.ds.server.openapi.model.CardTag;
import ru.msvdev.ds.server.module.value.base.DataType;

import java.util.List;


@Service
@RequiredArgsConstructor
public class TagService {

    private final TagRepository tagRepository;

    private final FieldService fieldService;
    private final DataService dataService;

    private final TagResponseMapper tagResponseMapper;


    public List<CardTag> getTags(Long catalogId, Long cardId) {
        return tagRepository
                .findAll(catalogId, cardId)
                .stream()
                .map(tagResponseMapper::convert)
                .toList();
    }


    @Transactional
    public List<CardTag> addTags(Long catalogId, Long cardId, List<CardTag> cardTags) {

        for (CardTag tag : cardTags) {
            long fieldId = tag.getFieldId();
            String value = tag.getValue();

            if (!fieldService.existsById(catalogId, fieldId)) {
                throw new RuntimeException("Запрашиваемого поля не существует");
            }

            DataType dataType = fieldService.getValueType(catalogId, fieldId);
            long valueId = dataService.put(dataType, value);

            if (tagRepository.exists(cardId, fieldId, valueId)) continue;

            if (!tagRepository.insert(cardId, fieldId, valueId)) {
                throw new RuntimeException("Тег вставить не удалось");
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

}
