package ru.msvdev.ds.server.data.entity;

import ru.msvdev.ds.server.sequrity.AuthorityType;

public record Catalog(
        long id,
        String name,
        String description,
        AuthorityType[] authorities
) {
}
