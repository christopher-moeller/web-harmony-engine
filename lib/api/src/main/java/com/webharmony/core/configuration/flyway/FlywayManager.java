package com.webharmony.core.configuration.flyway;

import lombok.Getter;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.output.CleanResult;

@Getter
public class FlywayManager {

    private final Flyway flyway;

    public FlywayManager(Flyway flyway) {
        this.flyway = flyway;
    }

    public void migrate() {
        this.flyway.migrate();
    }

    public CleanResult clean() {
        return this.flyway.clean();
    }
}
