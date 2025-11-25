CREATE TABLE app_application_startup_log
(
    uuid               UUID                        NOT NULL,
    started_at         TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    duration_in_millis BIGINT                      NOT NULL,
    duration_text      VARCHAR(255)                NOT NULL,
    CONSTRAINT pk_app_application_startup_log PRIMARY KEY (uuid)
);