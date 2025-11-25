package com.webharmony.core.utils.dev.fepages;

import com.webharmony.core.data.enums.ECoreActorRight;
import com.webharmony.core.utils.dev.fepages.model.*;

import java.util.List;

public class CorePageProvider extends PageProvider {

    @Override
    @SuppressWarnings("java:S1192")
    protected List<Page> createPages() {
        return List.of(
                Page.of("index", "", "Index", FrontendComponent.definedInFrontend(), PageAccessRule.publicAccess()),
                Page.of("confirmEmail", "confirmEmail", "Confirm E-Mail", FrontendComponent.definedInFrontend(), PageAccessRule.publicAccess()),
                Page.of("forbidden", "forbidden", "Forbidden", FrontendComponent.definedInFrontend(), PageAccessRule.publicAccess()),
                Page.of("pageNotFound", "pageNotFound", "Page not found", FrontendComponent.definedInFrontend(), PageAccessRule.publicAccess()),
                Page.of("forgotPassword", "forgotPassword", "Forgot Password", FrontendComponent.definedInFrontend(), PageAccessRule.publicAccess()),
                Page.of("login", "login", "Login", FrontendComponent.definedInFrontend(), PageAccessRule.publicAccess()),
                Page.of("register", "register", "Register", FrontendComponent.definedInFrontend(), PageAccessRule.publicAccess()),
                Page.of("registerByInvitation", "registerByInvitation", "Register by Invitation", FrontendComponent.definedInFrontend(), PageAccessRule.publicAccess()),
                Page.of("resetPasswordByEmail", "resetPasswordByEmail", "Reset Password By E-Mail", FrontendComponent.definedInFrontend(), PageAccessRule.publicAccess()),
                Page.of("about", "about", "About", FrontendComponent.definedInFrontend(), PageAccessRule.publicAccess()),
                Page.of("imprint", "imprint", "Imprint", FrontendComponent.definedInFrontend(), PageAccessRule.publicAccess()),
                Page.of("privacyNotice", "privacyNotice", "Privacy Notice", FrontendComponent.definedInFrontend(), PageAccessRule.publicAccess()),
                Page.of("language", "language", "Language", FrontendComponent.definedInFrontend(), PageAccessRule.publicAccess()),
                Page.of("account", "account", "Account", FrontendComponent.definedInFrontend(), PageAccessRule.requiresRight(ECoreActorRight.CORE_USER_PERSONAL_ACCOUNT_CRUD)),
                Page.virtual("app", "app").withSubPages(
                        Page.of("app-home", "home", "Home", FrontendComponent.definedInFrontend(), PageAccessRule.requiresRight(ECoreActorRight.CORE_HOME_ACCESS)),
                        Page.of("app-language", "language", "Language", FrontendComponent.definedInFrontend(), PageAccessRule.requiresRight(ECoreActorRight.CORE_USER_PERSONAL_ACCOUNT_CRUD)),
                        Page.of("app-account", "account", "Account", FrontendComponent.definedInFrontend(), PageAccessRule.requiresRight(ECoreActorRight.CORE_USER_PERSONAL_ACCOUNT_CRUD)),
                        Page.of("app-personalNotifications", "personalNotifications", "Personal Notifications", FrontendComponent.definedInFrontend(), PageAccessRule.requiresRight(ECoreActorRight.CORE_PERSONAL_NOTIFICATIONS)).withSubPages(
                                Page.of("app-personalNotifications-id", "[id]", "Notification", FrontendComponent.definedInFrontend(), PageAccessRule.requiresRight(ECoreActorRight.CORE_PERSONAL_NOTIFICATIONS))
                        ),
                        Page.of("app-users", "users", "Users", FrontendComponent.genericNavigationComponent(), PageAccessRule.collectFromChildren()).withSubPages(
                                Page.of("app-users-actorRights", "actorRights", "Rights", FrontendComponent.definedInFrontend(), PageAccessRule.requiresRight(ECoreActorRight.CORE_ACTOR_RIGHTS_CRUD)).withSubPages(
                                        Page.of("app-users-actorRights-id", "[id]", "Right", FrontendComponent.definedInFrontend(), PageAccessRule.requiresRight(ECoreActorRight.CORE_ACTOR_RIGHTS_CRUD))
                                ),
                                Page.of("app-users-registrations", "registrations", "Registrations", FrontendComponent.definedInFrontend(), PageAccessRule.requiresRight(ECoreActorRight.CORE_REGISTRATION_CRUD)).withSubPages(
                                        Page.of("app-users-registrations-id", "[id]", "Registrations", FrontendComponent.definedInFrontend(), PageAccessRule.requiresRight(ECoreActorRight.CORE_REGISTRATION_CRUD)),
                                        Page.of("app-users-registrations-sendInvitation", "sendInvitation", "Send Invitation", FrontendComponent.definedInFrontend(), PageAccessRule.requiresRight(ECoreActorRight.CORE_REGISTRATION_SEND_INVITATIONS))
                                ),
                                Page.of("app-users-userRoles", "userRoles", "Roles", FrontendComponent.definedInFrontend(), PageAccessRule.requiresRight(ECoreActorRight.CORE_USER_ROLES_CRUD)).withSubPages(
                                        Page.of("app-users-userRoles-id", "[id]", "Role", FrontendComponent.definedInFrontend(), PageAccessRule.requiresRight(ECoreActorRight.CORE_USER_ROLES_CRUD))
                                ),
                                Page.of("app-users-users", "users", "Users", FrontendComponent.definedInFrontend(), PageAccessRule.requiresRight(ECoreActorRight.CORE_USERS_CRUD)).withSubPages(
                                        Page.of("app-users-users-id", "[id]", "User", FrontendComponent.definedInFrontend(), PageAccessRule.requiresRight(ECoreActorRight.CORE_USERS_CRUD))
                                ),
                                Page.of("app-users-actors", "actors", "Actors", FrontendComponent.definedInFrontend(), PageAccessRule.requiresRight(ECoreActorRight.CORE_ACTOR_READ_ALL_ACTORS)).withSubPages(
                                        Page.of("app-users-actors-id", "[id]", "Actor", FrontendComponent.definedInFrontend(), PageAccessRule.requiresRight(ECoreActorRight.CORE_ACTOR_READ_ALL_ACTORS))
                                )
                        ),
                        Page.of("app-admin", "admin", "Admin", FrontendComponent.genericNavigationComponent(), PageAccessRule.collectFromChildren()).withSubPages(
                                Page.of("app-admin-applicationStatus", "applicationStatus", "Application Status", FrontendComponent.definedInFrontend(), PageAccessRule.requiresRightAtLeastOneRight(ECoreActorRight.CORE_APPLICATION_STATUS_RESET, ECoreActorRight.CORE_APPLICATION_STATUS_SAVE)),
                                Page.of("app-admin-applicationVersion", "applicationVersion", "Application Version", FrontendComponent.definedInFrontend(), PageAccessRule.requiresRight(ECoreActorRight.CORE_SERVER_INFO_READ)),
                                Page.of("app-admin-logs", "logs", "Logs", FrontendComponent.genericNavigationComponent(), PageAccessRule.collectFromChildren()).withSubPages(
                                        Page.of("app-admin-logs-api", "api", "API", FrontendComponent.definedInFrontend(), PageAccessRule.requiresRight(ECoreActorRight.CORE_LOGS_READ)),
                                        Page.of("app-admin-logs-startupLogs", "startupLogs", "Startup Logs", FrontendComponent.definedInFrontend(), PageAccessRule.requiresRight(ECoreActorRight.CORE_STARTUP_LOGS_CRUD)).withSubPages(
                                                Page.of("app-admin-logs-startupLogs-id", "[id]", "Startup Logs", FrontendComponent.definedInFrontend(), PageAccessRule.requiresRight(ECoreActorRight.CORE_STARTUP_LOGS_CRUD))
                                        )
                                ),
                                Page.of("app-admin-cronJobs", "cronJobs", "Cron Jobs", FrontendComponent.definedInFrontend(), PageAccessRule.requiresRight(ECoreActorRight.CORE_CRON_JOBS_CRUD)).withSubPages(
                                        Page.of("app-admin-cronJobs-id", "[id]", "Cron Job", FrontendComponent.definedInFrontend(), PageAccessRule.requiresRight(ECoreActorRight.CORE_CRON_JOBS_CRUD))
                                ),
                                Page.of("app-admin-emails", "emails", "E-Mails", FrontendComponent.definedInFrontend(), PageAccessRule.requiresRight(ECoreActorRight.CORE_EMAIL_CRUD_RIGHTS)).withSubPages(
                                        Page.of("app-admin-emails-id", "[id]", "E-Mail", FrontendComponent.definedInFrontend(), PageAccessRule.requiresRight(ECoreActorRight.CORE_EMAIL_CRUD_RIGHTS))
                                ),
                                Page.of("app-admin-files", "files", "Files", FrontendComponent.definedInFrontend(), PageAccessRule.requiresRight(ECoreActorRight.CORE_FILES_CRUD)).withSubPages(
                                        Page.of("app-admin-files-id", "[id]", "File", FrontendComponent.definedInFrontend(), PageAccessRule.requiresRight(ECoreActorRight.CORE_FILES_CRUD))
                                ),
                                Page.of("app-admin-harmonyEvents", "harmonyEvents", "Harmony Events", FrontendComponent.definedInFrontend(), PageAccessRule.requiresRight(ECoreActorRight.CORE_HARMONY_EVENT_CRUD)).withSubPages(
                                        Page.of("app-admin-harmonyEvents-id", "[id]", "Harmony Event", FrontendComponent.definedInFrontend(), PageAccessRule.requiresRight(ECoreActorRight.CORE_HARMONY_EVENT_CRUD))
                                ),
                                Page.of("app-admin-i18n", "i18n", "I18n", FrontendComponent.genericNavigationComponent(), PageAccessRule.collectFromChildren()).withSubPages(
                                        Page.of("app-admin-i18n-entityAttributes", "entityAttributes", "Entity Attributes", FrontendComponent.definedInFrontend(), PageAccessRule.requiresRight(ECoreActorRight.CORE_I18N_ENTITY_ATTRIBUTE_CRUD)).withSubPages(
                                                Page.of("app-admin-i18n-entityAttributes-id", "[id]", "Entity Attribute", FrontendComponent.definedInFrontend(), PageAccessRule.requiresRight(ECoreActorRight.CORE_I18N_ENTITY_ATTRIBUTE_CRUD))
                                        ),
                                        Page.of("app-admin-i18n-keyEntries", "keyEntries", "Key Entries", FrontendComponent.definedInFrontend(), PageAccessRule.requiresRight(ECoreActorRight.CORE_I18N_KEY_ENTRY_CRUD)).withSubPages(
                                                Page.of("app-admin-i18n-keyEntries-id", "[id]", "Key Entry", FrontendComponent.definedInFrontend(), PageAccessRule.requiresRight(ECoreActorRight.CORE_I18N_KEY_ENTRY_CRUD))
                                        )
                                ),
                                Page.of("app-admin-error", "error", "Error", FrontendComponent.genericNavigationComponent(), PageAccessRule.collectFromChildren()).withSubPages(
                                        Page.of("app-admin-error-applicationExceptions", "applicationExceptions", "Application Exceptions", FrontendComponent.definedInFrontend(), PageAccessRule.requiresRight(ECoreActorRight.CORE_APPLICATION_EXCEPTIONS_CRUD)).withSubPages(
                                                Page.of("app-admin-error-applicationExceptions-id", "[id]", "Application Exception", FrontendComponent.definedInFrontend(), PageAccessRule.requiresRight(ECoreActorRight.CORE_APPLICATION_EXCEPTIONS_CRUD))
                                        ),
                                        Page.of("app-admin-error-reportUserErrors", "reportUserErrors", "Report User Errors", FrontendComponent.definedInFrontend(), PageAccessRule.requiresRight(ECoreActorRight.CORE_REPORT_USER_ERRORS_CRUD)).withSubPages(
                                                Page.of("app-admin-error-reportUserErrors-id", "[id]", "Report User Error", FrontendComponent.definedInFrontend(), PageAccessRule.requiresRight(ECoreActorRight.CORE_REPORT_USER_ERRORS_CRUD))
                                        )
                                ),
                                Page.of("app-admin-notifications", "notifications", "Notifications", FrontendComponent.genericNavigationComponent(), PageAccessRule.collectFromChildren()).withSubPages(
                                        Page.of("app-admin-notifications-eventTypes", "eventTypes", "Event Types", FrontendComponent.definedInFrontend(), PageAccessRule.requiresRight(ECoreActorRight.CORE_NOTIFICATION_EVENT_TYPE_CRUD)).withSubPages(
                                                Page.of("app-admin-notifications-eventTypes-id", "[id]", "Event Type", FrontendComponent.definedInFrontend(), PageAccessRule.requiresRight(ECoreActorRight.CORE_NOTIFICATION_EVENT_TYPE_CRUD))
                                        ),
                                        Page.of("app-admin-notifications-notifications", "notifications", "Notifications", FrontendComponent.definedInFrontend(), PageAccessRule.requiresRight(ECoreActorRight.CORE_NOTIFICATIONS)).withSubPages(
                                                Page.of("app-admin-notifications-notifications-id", "[id]", "Notification", FrontendComponent.definedInFrontend(), PageAccessRule.requiresRight(ECoreActorRight.CORE_NOTIFICATIONS))
                                        )
                                ),
                                Page.of("app-admin-webContent", "webContent", "Web Content", FrontendComponent.genericNavigationComponent(), PageAccessRule.collectFromChildren()).withSubPages(
                                        Page.of("app-admin-webContent-webContents", "webContents", "Web Content", FrontendComponent.definedInFrontend(), PageAccessRule.requiresRight(ECoreActorRight.CORE_WEB_CONTENT_CRUD)).withSubPages(
                                                Page.of("app-admin-webContent-webContents-id", "[id]", "Web Content", FrontendComponent.definedInFrontend(), PageAccessRule.requiresRight(ECoreActorRight.CORE_WEB_CONTENT_CRUD))
                                        ),
                                        Page.of("app-admin-webContent-webContentAreas", "webContentAreas", "Areas", FrontendComponent.definedInFrontend(), PageAccessRule.requiresRight(ECoreActorRight.CORE_WEB_CONTENT_AREA_CRUD)).withSubPages(
                                                Page.of("app-admin-webContent-webContentAreas-id", "[id]", "Areas", FrontendComponent.definedInFrontend(), PageAccessRule.requiresRight(ECoreActorRight.CORE_WEB_CONTENT_AREA_CRUD))
                                        )
                                ),
                                Page.of("app-admin-registryItems", "registryItems", "Registry Items", FrontendComponent.definedInFrontend(), PageAccessRule.requiresRight(ECoreActorRight.CORE_REGISTRY_CRUD)).withSubPages(
                                        Page.of("app-admin-registryItems-id", "[id]", "Registry Item", FrontendComponent.definedInFrontend(), PageAccessRule.requiresRight(ECoreActorRight.CORE_REGISTRY_CRUD))
                                ),
                                Page.of("app-admin-secureKeys", "secureKeys", "Secure Keys", FrontendComponent.definedInFrontend(), PageAccessRule.requiresRight(ECoreActorRight.CORE_SECURE_KEYS_CRUD)).withSubPages(
                                        Page.of("app-admin-secureKeys-id", "[id]", "Secure Key", FrontendComponent.definedInFrontend(), PageAccessRule.requiresRight(ECoreActorRight.CORE_SECURE_KEYS_CRUD))
                                ),
                                Page.of("app-admin-serverTasks", "serverTasks", "Server Tasks", FrontendComponent.definedInFrontend(), PageAccessRule.requiresRight(ECoreActorRight.CORE_SERVER_TASKS_CRUD)).withSubPages(
                                        Page.of("app-admin-serverTasks-id", "[id]", "Server Task", FrontendComponent.definedInFrontend(), PageAccessRule.requiresRight(ECoreActorRight.CORE_SERVER_TASKS_CRUD))
                                )
                        )
                )
        );
    }

    @Override
    @SuppressWarnings("java:S1192")
    protected NavigationTree createNavigationTree() {
        return NavigationTree.ofItems(
                NavigationItem.toPage(getPageById("app-home"), "bx-home"),
                NavigationItem.toPage(getPageById("app-users"), "bx-group").withChildren(
                        NavigationItem.toPage(getPageById("app-users-actorRights"), "bx-paragraph").withChildren(
                                NavigationItem.toPage(getPageById("app-users-actorRights-id"), "bx-paragraph")
                        ),
                        NavigationItem.toPage(getPageById("app-users-registrations"), "bx-user-plus").withChildren(
                                NavigationItem.toPage(getPageById("app-users-registrations-id"), "bx-user-plus"),
                                NavigationItem.toPage(getPageById("app-users-registrations-sendInvitation"), "bx-user-plus")
                        ),
                        NavigationItem.toPage(getPageById("app-users-userRoles"), "bxs-group").withChildren(
                                NavigationItem.toPage(getPageById("app-users-userRoles-id"), "bxs-group")
                        ),
                        NavigationItem.toPage(getPageById("app-users-users"), "bxs-user-detail").withChildren(
                                NavigationItem.toPage(getPageById("app-users-users-id"), "bxs-user-detail")
                        ),
                        NavigationItem.toPage(getPageById("app-users-actors"), "bx-universal-access").withChildren(
                                NavigationItem.toPage(getPageById("app-users-actors-id"), "bx-universal-access")
                        )
                ),
                NavigationItem.toPage(getPageById("app-admin"), "bx-cog").withChildren(
                        NavigationItem.toPage(getPageById("app-admin-applicationStatus"), "bxs-traffic"),
                        NavigationItem.toPage(getPageById("app-admin-logs"), "bxs-spreadsheet").withChildren(
                                NavigationItem.toPage(getPageById("app-admin-logs-api"), "bxs-spreadsheet"),
                                NavigationItem.toPage(getPageById("app-admin-logs-startupLogs"), "bx-run").withChildren(
                                        NavigationItem.toPage(getPageById("app-admin-logs-startupLogs-id"), "bx-run")
                                )
                        ),
                        NavigationItem.toPage(getPageById("app-admin-applicationVersion"), "bx-git-compare"),
                        NavigationItem.toPage(getPageById("app-admin-cronJobs"), "bx-repeat").withChildren(
                                NavigationItem.toPage(getPageById("app-admin-cronJobs-id"), "bx-repeat")
                        ),
                        NavigationItem.toPage(getPageById("app-admin-emails"), "bx-envelope").withChildren(
                                NavigationItem.toPage(getPageById("app-admin-emails-id"), "bx-envelope")
                        ),
                        NavigationItem.toPage(getPageById("app-admin-files"), "bx-file-blank").withChildren(
                                NavigationItem.toPage(getPageById("app-admin-files-id"), "bx-file-blank")
                        ),
                        NavigationItem.toPage(getPageById("app-admin-harmonyEvents"), "bxs-hot").withChildren(
                                NavigationItem.toPage(getPageById("app-admin-harmonyEvents-id"), "bxs-hot")
                        ),
                        NavigationItem.toPage(getPageById("app-admin-error"), "bx-bug-alt").withChildren(
                                NavigationItem.toPage(getPageById("app-admin-error-applicationExceptions"), "bxs-error").withChildren(
                                        NavigationItem.toPage(getPageById("app-admin-error-applicationExceptions-id"), "bxs-error")
                                ),
                                NavigationItem.toPage(getPageById("app-admin-error-reportUserErrors"), "bxs-user-voice").withChildren(
                                        NavigationItem.toPage(getPageById("app-admin-error-reportUserErrors-id"), "bxs-user-voice")
                                )
                        ),
                        NavigationItem.toPage(getPageById("app-admin-i18n"), "bxs-plane-alt").withChildren(
                                NavigationItem.toPage(getPageById("app-admin-i18n-entityAttributes"), "bxs-key").withChildren(
                                        NavigationItem.toPage(getPageById("app-admin-i18n-entityAttributes-id"), "bxs-key")
                                ),
                                NavigationItem.toPage(getPageById("app-admin-i18n-keyEntries"), "bx-table").withChildren(
                                        NavigationItem.toPage(getPageById("app-admin-i18n-keyEntries-id"), "bx-table")
                                )
                        ),
                        NavigationItem.toPage(getPageById("app-admin-notifications"), "bxs-bell").withChildren(
                                NavigationItem.toPage(getPageById("app-admin-notifications-eventTypes"), "bx-notification").withChildren(
                                        NavigationItem.toPage(getPageById("app-admin-notifications-eventTypes-id"), "bx-notification")
                                ),
                                NavigationItem.toPage(getPageById("app-admin-notifications-notifications"), "bxs-envelope").withChildren(
                                        NavigationItem.toPage(getPageById("app-admin-notifications-notifications-id"), "bxs-envelope")
                                )
                        ),
                        NavigationItem.toPage(getPageById("app-admin-webContent"), "bxl-internet-explorer").withChildren(
                                NavigationItem.toPage(getPageById("app-admin-webContent-webContents"), "bxl-internet-explorer").withChildren(
                                        NavigationItem.toPage(getPageById("app-admin-webContent-webContents-id"), "bx-notification")
                                ),
                                NavigationItem.toPage(getPageById("app-admin-webContent-webContentAreas"), "bx-category").withChildren(
                                        NavigationItem.toPage(getPageById("app-admin-webContent-webContentAreas-id"), "bxs-envelope")
                                )
                        ),
                        NavigationItem.toPage(getPageById("app-admin-registryItems"), "bx-edit-alt").withChildren(
                                NavigationItem.toPage(getPageById("app-admin-registryItems-id"), "bx-edit-alt")
                        ),
                        NavigationItem.toPage(getPageById("app-admin-secureKeys"), "bx-key").withChildren(
                                NavigationItem.toPage(getPageById("app-admin-secureKeys-id"), "bx-key")
                        ),
                        NavigationItem.toPage(getPageById("app-admin-serverTasks"), "bx-task").withChildren(
                                NavigationItem.toPage(getPageById("app-admin-serverTasks-id"), "bx-task")
                        )
                )
        );
    }

}
