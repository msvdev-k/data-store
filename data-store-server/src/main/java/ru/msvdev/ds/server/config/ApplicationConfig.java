package ru.msvdev.ds.server.config;

import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Configuration;
import ru.msvdev.ds.server.DataStoreServerApplication;


@Configuration
@ConfigurationPropertiesScan(basePackageClasses = DataStoreServerApplication.class)
public class ApplicationConfig {
}
