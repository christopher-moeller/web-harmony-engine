package com.webharmony.core.data.enums;

import com.webharmony.core.data.enums.utils.PersistenceEnum;
import com.webharmony.core.data.jpa.model.user.AppActorRight;
import com.webharmony.core.i18n.I18N;
import com.webharmony.core.configuration.security.ApplicationRight;
import lombok.Getter;

public enum ECoreActorRight implements PersistenceEnum<AppActorRight>, ApplicationRight {

    CORE_ACTOR_READ_ALL_ACTORS("Actors read all", "The user is allowed to read all actors of the system."),
    CORE_ACTOR_RIGHTS_CRUD("Actor-Rights CRUD", "The user is allowed to apply all CRUD operations on Actor-Rights"),
    CORE_APPLICATION_EXCEPTIONS_CRUD("Application-Exceptions CRUD", "The user is allowed to apply all CRUD operations on Application-Exceptions"),
    CORE_APPLICATION_STATUS_RESET("Application-Status reset", "The user is allowed to reset application status."),
    CORE_APPLICATION_STATUS_SAVE("Application-Status update", "The user is allowed to update application status."),
    CORE_APP_ACCESS("App-Access", "The user is allowed to access the 'App-Layer' of the system."),
    CORE_AUTHENTICATION_OWN_ACTOR("Load own Actor", "The user is allowed to load all information to his own Actor instance."),
    CORE_CRON_JOBS_CRUD("Cron-Jobs CRUD", "The user is allowed to apply all CRUD operations on Cron-Jobs"),
    CORE_CRON_JOBS_EXECUTE("Cron-Job execution", "The user is allowed to execute a specific Cron-Job manually."),
    CORE_EMAIL_CRUD_RIGHTS("E-Mails CRUD", "The user is allowed to apply all CRUD operations on E-Mails."),
    CORE_EMAIL_SEND_BY_ENTITY("E-Mails send by Entity", "The user is allowed to send or resend a E-Mail by a specific Entity representation in the Database."),
    CORE_ERROR_MESSAGE_FOR_VALIDATION_READ("error message for validation read", "actor is allowed to see the message for validation errors"),
    CORE_ERROR_MESSAGE_DETAIL_READ("Error-Message details read", "The user is allowed to read more detailed information from a Error-Message."),
    CORE_ERROR_JAVA_STACKTRACE_READ("Error-Message Java Stacktrace read ", "The user is allowed to read the Java Stacktrace from a Error-Message."),
    CORE_FILES_CRUD("Files CRUD", "The user is allowed to apply all CRUD operations on Files."),
    CORE_HARMONY_EVENT_CRUD("Harmony-Event CRUD", "The user is allowed to apply all CRUD operations on Harmony-Event."),
    CORE_HOME_ACCESS("Home access", "The user is allowed to access the page 'home' in the 'App-Layer'."),
    CORE_I18N_ENTITY_ATTRIBUTE_CRUD("I18n-Entity-Attribute CRUD ", "The user is allowed to apply all CRUD operations on I18n-Entity-Attribute."),
    CORE_I18N_KEY_ENTRY_CRUD("I18n-Key-Entry CRUD", "The user is allowed to apply all CRUD operations on I18n-Key-Entry."),
    CORE_LOAD_SELECTABLE_RIGHTS_FOR_OPTION_CONTAINER("Selectable Actor-Rights via option container", "The user is allowed to load all selectable Actor-Rights via the option container."),
    CORE_LOAD_SELECTABLE_ROLES_FOR_OPTION_CONTAINER("Selectable User-Roles via option container", "The user is allowed to load all selectable User-Roles via the option container."),
    CORE_LOGS_READ("Api-Logs read", "The user is allowed to read all Api-Logs."),
    CORE_NOTIFICATIONS("Notifications CRUD", "The user is allowed to apply all CRUD operations on Notifications."),
    CORE_NOTIFICATION_EVENT_TYPE_CRUD("Notification-Event-Type CRUD", "The user is allowed to apply all CRUD operations on Notification-Event-Type."),
    CORE_PERSONAL_NOTIFICATIONS("Personal-Notifications CRUD", "The user is allowed to apply all CRUD operations on Personal-Notifications."),
    CORE_REGISTRATION_ADMIN_CONFIRMATION("Admin confirmation of Registration-Requests", "The user is allowed to confirm Registration-Requests for new users."),
    CORE_REGISTRATION_CRUD("Registrations CRUD", "The user is allowed to apply all CRUD operations on Registrations."),
    CORE_REGISTRATION_SEND_INVITATIONS("Send invitation for Registration", "The user is allowed to send a invitation to Registrations."),
    CORE_REGISTRY_CRUD("Registry CRUD", "The user is allowed to apply all CRUD operations on Registry-Items."),
    CORE_REPORT_USER_ERRORS_CRUD("Reported-User-Errors CRUD", "The user is allowed to apply all CRUD operations on Reported-User-Errors."),
    CORE_SERVER_TASKS_CRUD("Server-Tasks CRUD", "The user is allowed to apply all CRUD operations on Server-Tasks."),
    CORE_SERVER_TASKS_EXECUTE("Server-Tasks execute", "The user is allowed to execute a Server-Task."),
    CORE_SERVER_INFO_READ("Server info read", "The user is allowed to read the server info (e.g. versions)."),
    CORE_SERVER_STATUS_NOT_OK_USER_HAS_ACCESS("Application access on maintenance mode", "If the server status is not ok, this right controls if the user has still access to to non public APIs."),
    CORE_STARTUP_LOGS_CRUD("Startup-Logs CRUD", "The user is allowed to apply all CRUD operations on Startup-Logs."),
    CORE_USERS_CRUD("Users CRUD", "The user is allowed to apply all CRUD operations on Users."),
    CORE_USER_PERSONAL_ACCOUNT_CRUD("Personal User-Account CRUD", "The user is allowed to apply all CRUD operations on the personal User-Account."),
    CORE_USER_ROLES_CRUD("User-Roles CRUD", "The user is allowed to apply all CRUD operations on User-Roles."),
    CORE_SECURE_KEYS_CRUD("Secured-Keys CRUD", "The user is allowed to apply all CRUD operations on Secured-Keys."),
    CORE_WEB_CONTENT_CRUD("Web-Content CRUD", "The user is allowed to apply all CRUD operations on Web-Contents."),
    CORE_WEB_CONTENT_AREA_CRUD("Web-Content-Area CRUD", "The user is allowed to apply all CRUD operations on Web-Content-Areas.")
    ;

    @Getter
    private final String label;
    private final String description;

    ECoreActorRight(String label, String description) {
        this.label = label;
        this.description = description;
    }


    @Override
    public void initEntity(AppActorRight entity) {
        entity.setLabel(I18N.entityAttribute(I18N.CODING_LANGUAGE, label));
        entity.setDescription(I18N.entityAttribute(I18N.CODING_LANGUAGE, description));
    }

    @Override
    public Class<AppActorRight> getEntityClass() {
        return AppActorRight.class;
    }

}
