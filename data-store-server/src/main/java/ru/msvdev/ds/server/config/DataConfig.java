package ru.msvdev.ds.server.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import ru.msvdev.ds.server.dao.repository.RepositoryPackageMarker;


@Configuration
@EnableTransactionManagement
@EnableJdbcRepositories(basePackageClasses = RepositoryPackageMarker.class)
public class DataConfig {
}
