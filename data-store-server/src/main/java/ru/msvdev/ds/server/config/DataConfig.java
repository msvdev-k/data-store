package ru.msvdev.ds.server.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import ru.msvdev.ds.server.DataStoreServerApplication;


@Configuration
@EnableTransactionManagement
@EnableJdbcRepositories(basePackageClasses = DataStoreServerApplication.class)
public class DataConfig {
}
