package ru.msvdev.ds.client.model.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.UUID;


@Getter
@EqualsAndHashCode
@Builder
@AllArgsConstructor
public class User {

    private final UUID uuid;
}
