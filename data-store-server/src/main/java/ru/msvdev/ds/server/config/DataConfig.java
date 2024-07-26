package ru.msvdev.ds.server.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import ru.msvdev.ds.server.dao.repository.RepositoryPackageMarker;
import ru.msvdev.ds.server.module.catalog.repository.CatalogRepository;
import ru.msvdev.ds.server.module.field.repository.FieldRepository;
import ru.msvdev.ds.server.module.field.repository.FieldTypeRepository;
import ru.msvdev.ds.server.module.user.repository.UserAuthorityRepository;


@Configuration
@EnableTransactionManagement
@EnableJdbcRepositories(basePackageClasses = {
        CatalogRepository.class,
        UserAuthorityRepository.class,
        FieldRepository.class,
        FieldTypeRepository.class,
        RepositoryPackageMarker.class
})
public class DataConfig {
}
