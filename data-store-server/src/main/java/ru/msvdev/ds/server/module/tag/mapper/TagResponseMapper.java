package ru.msvdev.ds.server.module.tag.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.msvdev.ds.server.module.tag.entity.Tag;
import ru.msvdev.ds.server.module.value.service.DataService;
import ru.msvdev.ds.server.openapi.model.CardTag;

@Component
@RequiredArgsConstructor
public class TagResponseMapper {

    private final DataService dataService;


    public CardTag convert(Tag tag) {
        String value = dataService.get(tag.dataType(), tag.valueId());

        CardTag cardTag = new CardTag();
        cardTag.setFieldId(tag.fieldId());
        cardTag.setValue(value);

        return cardTag;
    }
}
