package ru.msvdev.ds.server.security;

import org.springframework.http.HttpMethod;

import java.util.Set;
import java.util.UUID;

public record HttpRequest(

        UUID userUuid,
        String[] pathParts,
        HttpMethod httpMethod,
        Set<Authority> authorities

) {
}
