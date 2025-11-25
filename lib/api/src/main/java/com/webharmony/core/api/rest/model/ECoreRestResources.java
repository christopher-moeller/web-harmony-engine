package com.webharmony.core.api.rest.model;

import com.webharmony.core.api.rest.model.utils.AbstractResourceDto;
import com.webharmony.core.api.rest.model.webcontent.WebContentDto;
import lombok.Getter;

import java.util.function.Supplier;

@Getter
public enum ECoreRestResources implements RestResourceInfo {

    ACTORS("actors"),
    APPLICATION_EXCEPTIONS("applicationExceptions"),
    CRON_JOBS("cronJobs"),
    EMAILS("emails", EmailDto::createInitialTemplate),
    FILES("files"),
    HARMONY_EVENTS("harmonyEvents"),
    I18N_ENTITY_ATTRIBUTE("i18nEntityAttribute"),
    I18N_KEY_ENTRIES("i18nKeyEntries"),
    NOTIFICATIONS("notifications"),
    NOTIFICATION_EVENT_TYPES("notificationEventTypes"),
    PERSONAL_NOTIFICATIONS("personalNotifications"),
    USERS("users", UserDto::createInitialTemplate),
    USER_REGISTRATIONS("userRegistrations"),
    REGISTRY_ITEMS("registryItems"),
    REPORT_USER_ERRORS("reportUserErrors"),
    RIGHTS("actorRights"),
    ROLES("userRoles", UserRoleDto::createInitialTemplate),
    SECURE_KEY("secureKeys"),
    SERVER_TASKS("serverTasks"),
    STARTUP_LOGS("startupLogs"),
    WEB_CONTENTS("webContents", WebContentDto::createInitialTemplate),
    WEB_CONTENT_AREAS("webContentAreas");

    private final String id;

    private final Supplier<AbstractResourceDto> templateSupplier;

    ECoreRestResources(String id) {
        this.id = id;
        templateSupplier = null;
    }

    ECoreRestResources(String id, Supplier<AbstractResourceDto> templateSupplier) {
        this.id = id;
        this.templateSupplier = templateSupplier;
    }

}
