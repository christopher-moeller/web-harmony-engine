package com.webharmony.core.configuration.utils;

public class EnvironmentConstants {

    private EnvironmentConstants() {

    }

    public static final String PROFILE_DEV = "dev";
    public static final String PROFILE_TEST = "test";
    public static final String PROFILE_PROD = "prod";

    // ############################################################
    public static final String ENV_APP_ARTIFACT_ID = "app.artifactId";
    public static final String ENV_APP_NAME = "app.name";
    public static final String ENV_APP_SHORT_NAME = "app.shortName";
    public static final String ENV_APP_LONG_NAME = "app.longName";
    public static final String ENV_APP_VERSION = "app.version";
    public static final String ENV_BACKEND_CUSTOM_DOMAIN = "backend.custom-domain";
    public static final String ENV_BACKEND_USE_CUSTOM_DOMAIN = "backend.use-custom-domain";
    public static final String ENV_BACKEND_USE_SSL = "backend.use-ssl";
    public static final String ENV_SPRING_PROFILES_ACTIVE = "spring.profiles.active";
    public static final String ENV_AUTHENTICATION_DEFAULT_ADMIN_EMAIL = "authentication.default-admin-email";
    public static final String ENV_AUTHENTICATION_JWT_ALLOW_COOKIE = "authentication.jwt.allow-in-cookie";
    public static final String ENV_AUTHENTICATION_JWT_EXPIRATION_HOURS = "authentication.jwt.expirationHours";
    public static final String ENV_AUTHENTICATION_JWT_JWT_TYPE = "authentication.jwt.jwtType";
    public static final String ENV_LOG_LOG_API_CALLS = "log.log-api-calls";
    public static final String ENV_DB_ALLOW_CLEAN_WITH_FLYWAY = "db.allow-clean-with-flyway";

}
