package ru.msvdev.ds.client.cartalog;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import ru.msvdev.ds.client.openapi.api.CatalogApi;
import ru.msvdev.ds.client.openapi.model.CatalogResponse;

import java.util.UUID;


@EqualsAndHashCode
@AllArgsConstructor
@Builder(builderClassName = "BaseCatalogBuilder")
public class Catalog {

    private final CatalogApi catalogApi;
    private final UUID userUuid;

    private Long id;
    @Getter
    private String name;
    @Getter
    private String description;


    public static CatalogBuilder builder() {
        return new CatalogBuilder();
    }

    public static class CatalogBuilder extends BaseCatalogBuilder {
        public CatalogBuilder catalogResponse(CatalogResponse catalogResponse) {
            this.id(catalogResponse.getId());
            this.name(catalogResponse.getName());
            this.description(catalogResponse.getDescription());
            return this;
        }
    }
}
