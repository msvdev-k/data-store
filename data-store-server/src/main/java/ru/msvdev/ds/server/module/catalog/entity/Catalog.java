package ru.msvdev.ds.server.module.catalog.entity;

import ru.msvdev.ds.server.security.Authority;

public record Catalog(
        long id,
        String name,
        String description,
        Authority[] authorities
) {
}
