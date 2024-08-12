package ru.msvdev.ds.client.provider;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;


@Getter
@Builder
@AllArgsConstructor
public class ProviderConfiguration {

    private String scheme;
    private String host;
    private int port;

}
