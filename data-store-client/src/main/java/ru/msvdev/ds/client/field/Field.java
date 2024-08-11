package ru.msvdev.ds.client.field;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import ru.msvdev.ds.client.base.Validated;
import ru.msvdev.ds.client.openapi.model.FieldResponse;
import ru.msvdev.ds.client.openapi.model.FieldTypes;


@EqualsAndHashCode
@AllArgsConstructor
@Builder(builderClassName = "DefaultFieldBuilder")
public class Field implements Validated {

    private Long id;
    @Getter
    private Integer order;
    @Getter
    private String name;
    @Getter
    private String description;
    @Getter
    private FieldTypes type;
    @Getter
    private String format;


    @Override
    public boolean isValid() {
        return id != null;
    }


    public static FieldBuilder builder() {
        return new FieldBuilder();
    }

    public static class FieldBuilder extends DefaultFieldBuilder {
        public FieldBuilder fieldResponse(FieldResponse fieldResponse) {
            this.id(fieldResponse.getId());
            this.order(fieldResponse.getOrder());
            this.name(fieldResponse.getName());
            this.description(fieldResponse.getDescription());
            this.type(fieldResponse.getType());
            this.format(fieldResponse.getFormat());
            return this;
        }
    }
}
