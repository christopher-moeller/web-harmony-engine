CREATE TABLE app_web_content_area
(
    uuid                              UUID NOT NULL,
    unique_name                       VARCHAR(255),
    created_by                        VARCHAR(255),
    updated_by                        VARCHAR(255),
    created_at                        TIMESTAMP WITHOUT TIME ZONE,
    updated_at                        TIMESTAMP WITHOUT TIME ZONE,
    label                             UUID,
    description                       UUID,
    required_read_right               UUID,
    required_write_right              UUID,
    only_one_content_instance_allowed BOOLEAN,
    CONSTRAINT pk_app_web_content_area PRIMARY KEY (uuid)
);

ALTER TABLE app_web_content_area
    ADD CONSTRAINT FK_APP_WEB_CONTENT_AREA_ON_DESCRIPTION FOREIGN KEY (description) REFERENCES app_i18n_entity_attribute (uuid);

ALTER TABLE app_web_content_area
    ADD CONSTRAINT FK_APP_WEB_CONTENT_AREA_ON_LABEL FOREIGN KEY (label) REFERENCES app_i18n_entity_attribute (uuid);

ALTER TABLE app_web_content_area
    ADD CONSTRAINT FK_APP_WEB_CONTENT_AREA_ON_REQUIRED_READ_RIGHT FOREIGN KEY (required_read_right) REFERENCES app_actor_right (uuid);

ALTER TABLE app_web_content_area
    ADD CONSTRAINT FK_APP_WEB_CONTENT_AREA_ON_REQUIRED_WRITE_RIGHT FOREIGN KEY (required_write_right) REFERENCES app_actor_right (uuid);

CREATE TABLE app_file__web_content
(
    file_id        UUID NOT NULL,
    web_content_id UUID NOT NULL,
    CONSTRAINT pk_app_file__web_content PRIMARY KEY (file_id, web_content_id)
);

CREATE TABLE app_web_content
(
    uuid         UUID NOT NULL,
    created_by   VARCHAR(255),
    updated_by   VARCHAR(255),
    created_at   TIMESTAMP WITHOUT TIME ZONE,
    updated_at   TIMESTAMP WITHOUT TIME ZONE,
    label        VARCHAR(255),
    json_content TEXT,
    area         UUID,
    CONSTRAINT pk_app_web_content PRIMARY KEY (uuid)
);

ALTER TABLE app_web_content
    ADD CONSTRAINT FK_APP_WEB_CONTENT_ON_AREA FOREIGN KEY (area) REFERENCES app_web_content_area (uuid);

ALTER TABLE app_file__web_content
    ADD CONSTRAINT fk_appfilwebcon_on_app_file FOREIGN KEY (file_id) REFERENCES app_file (uuid);

ALTER TABLE app_file__web_content
    ADD CONSTRAINT fk_appfilwebcon_on_app_web_content FOREIGN KEY (web_content_id) REFERENCES app_web_content (uuid);