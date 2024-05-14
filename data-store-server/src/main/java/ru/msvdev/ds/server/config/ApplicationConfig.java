package ru.msvdev.ds.server.config;

import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Configuration;
import ru.msvdev.ds.server.property.PropertyPackageMarker;


@Configuration
@ConfigurationPropertiesScan(basePackageClasses = PropertyPackageMarker.class)
public class ApplicationConfig {
}
