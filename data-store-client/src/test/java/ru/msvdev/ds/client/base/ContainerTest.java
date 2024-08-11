package ru.msvdev.ds.client.base;

import org.junit.jupiter.api.Test;

import static ru.msvdev.ds.client.base.DataStoreContainerTest.*;


public class ContainerTest {

    @Test
    void dataStoreHostTest() {
        System.out.printf("Data Store Host: %s\n", dataStoreHost());
    }

    @Test
    void dataStorePortTest() {
        System.out.printf("Data Store Port: %d\n", dataStorePort());
    }

    @Test
    void dataStoreUrlTest() {
        System.out.printf("Data Store URL: %s\n", dataStoreUrl());
    }
}
