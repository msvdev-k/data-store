package ru.msvdev.ds.client.model.card;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import ru.msvdev.ds.client.base.Validated;
import ru.msvdev.ds.client.factory.BuilderFactory;
import ru.msvdev.ds.client.model.cartalog.Catalog;
import ru.msvdev.ds.client.model.field.Field;
import ru.msvdev.ds.client.model.tag.Tag;
import ru.msvdev.ds.client.model.user.User;
import ru.msvdev.ds.client.openapi.api.CardApi;
import ru.msvdev.ds.client.openapi.api.TagApi;
import ru.msvdev.ds.client.openapi.model.CardResponse;
import ru.msvdev.ds.client.openapi.model.CardTag;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;


@EqualsAndHashCode
@AllArgsConstructor
@Builder(builderClassName = "DefaultCardBuilder")
public class Card implements Validated {

    private final BuilderFactory builderFactory;

    private final CardApi cardApi;
    private final TagApi tagApi;

    @Getter
    private final User authUser;

    @Getter
    private final Catalog catalog;

    @Getter
    private Long id;
    private List<Tag> tags;


    public List<Tag> getTags() {
        return tags.stream().toList();
    }


    @Override
    public boolean isValid() {
        return id != null;
    }


    public static CardBuilder builder() {
        return new CardBuilder();
    }

    public static class CardBuilder extends DefaultCardBuilder {
        public CardBuilder cardResponse(CardResponse response, Function<Long, Field> fieldById, Supplier<Tag.TagBuilder> tagBuilder) {
            List<Tag> tags;

            List<CardTag> cardTags = response.getTags();
            if (cardTags != null) {
                tags = response.getTags().stream()
                        .map(cardTag -> tagBuilder.get().cardTag(cardTag, fieldById).build())
                        .toList();
            } else {
                tags = new ArrayList<>();
            }

            this.id(response.getId());
            this.tags(tags);

            return this;
        }
    }
}
