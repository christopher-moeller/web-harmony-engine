package com.webharmony.core.configuration.flyway;

import org.flywaydb.core.api.configuration.ClassicConfiguration;

import javax.sql.DataSource;

public class FlywayCustomClassicConfiguration extends ClassicConfiguration {

    private final DataSource dataSource;

    public FlywayCustomClassicConfiguration(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public DataSource getDataSource() {
        return dataSource;
    }
}
