package ru.msvdev.ds.server.module.catalog.mapper;

import org.springframework.stereotype.Component;
import ru.msvdev.ds.server.module.catalog.entity.Catalog;
import ru.msvdev.ds.server.openapi.model.CatalogAuthority;
import ru.msvdev.ds.server.openapi.model.CatalogResponse;
import ru.msvdev.ds.server.security.Authority;

import java.util.ArrayList;
import java.util.Arrays;


@Component
public class CatalogResponseMapper {

    public CatalogResponse convert(Catalog catalog) {
        CatalogResponse catalogResponse = new CatalogResponse();
        catalogResponse.setId(catalog.id());
        catalogResponse.setName(catalog.name());
        catalogResponse.setDescription(catalog.description());

        if (catalog.authorities() != null) {
            catalogResponse.setAuthorities(
                    Arrays.stream(catalog.authorities())
                            .map(Authority::name)
                            .map(CatalogAuthority::valueOf)
                            .toList()
            );

        } else {
            catalogResponse.setAuthorities(new ArrayList<>());
        }

        return catalogResponse;
    }
}
