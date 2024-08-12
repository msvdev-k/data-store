package ru.msvdev.ds.client.model.tag;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import ru.msvdev.ds.client.model.field.Field;
import ru.msvdev.ds.client.openapi.model.CardTag;

import java.util.function.Function;


@EqualsAndHashCode
@AllArgsConstructor
@Builder(builderClassName = "DefaultTagBuilder")
public class Tag {

    @Getter
    private final Field field;
    @Getter
    private String stringValue;


    public static TagBuilder builder() {
        return new TagBuilder();
    }

    public static class TagBuilder extends DefaultTagBuilder {
        public TagBuilder cardTag(CardTag cardTag, Function<Long, Field> fieldById) {
            this.field(fieldById.apply(cardTag.getFieldId()));
            this.stringValue(cardTag.getValue());
            return this;
        }
    }
}
