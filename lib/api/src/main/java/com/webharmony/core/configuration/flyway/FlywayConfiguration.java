package com.webharmony.core.configuration.flyway;

import com.webharmony.core.configuration.utils.EnvironmentConstants;
import org.flywaydb.core.Flyway;
import org.springframework.boot.autoconfigure.flyway.FlywayMigrationStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import javax.sql.DataSource;
import java.util.Optional;

@Configuration
public class FlywayConfiguration {

    @Bean
    public FlywayManager flywayManager(DataSource dataSource, Environment environment) {

        final Flyway flyway = Flyway.configure()
                .configuration(new FlywayCustomClassicConfiguration(dataSource))
                .baselineOnMigrate(true)
                .cleanDisabled(!Optional.ofNullable(environment.getProperty(EnvironmentConstants.ENV_DB_ALLOW_CLEAN_WITH_FLYWAY, Boolean.class)).orElse(false))
                .load();

        return new FlywayManager(flyway);
    }

    @Bean
    public FlywayMigrationStrategy flywayMigrationStrategy(FlywayManager flywayManager) {
        return flywayOld -> flywayManager.migrate();
    }
}
