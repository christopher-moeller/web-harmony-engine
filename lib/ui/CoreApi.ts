import { AxiosResponse } from "axios"

// ################ Model declaration start ################

export enum ECoreRouterPage {
	INDEX = "index",
	CONFIRMEMAIL = "confirmEmail",
	FORBIDDEN = "forbidden",
	PAGENOTFOUND = "pageNotFound",
	FORGOTPASSWORD = "forgotPassword",
	LOGIN = "login",
	REGISTER = "register",
	REGISTERBYINVITATION = "registerByInvitation",
	RESETPASSWORDBYEMAIL = "resetPasswordByEmail",
	ABOUT = "about",
	IMPRINT = "imprint",
	PRIVACYNOTICE = "privacyNotice",
	LANGUAGE = "language",
	ACCOUNT = "account",
	APP_HOME = "app-home",
	APP_LANGUAGE = "app-language",
	APP_ACCOUNT = "app-account",
	APP_PERSONALNOTIFICATIONS = "app-personalNotifications",
	APP_PERSONALNOTIFICATIONS_ID = "app-personalNotifications-id",
	APP_USERS = "app-users",
	APP_USERS_ACTORRIGHTS = "app-users-actorRights",
	APP_USERS_ACTORRIGHTS_ID = "app-users-actorRights-id",
	APP_USERS_REGISTRATIONS = "app-users-registrations",
	APP_USERS_REGISTRATIONS_ID = "app-users-registrations-id",
	APP_USERS_REGISTRATIONS_SENDINVITATION = "app-users-registrations-sendInvitation",
	APP_USERS_USERROLES = "app-users-userRoles",
	APP_USERS_USERROLES_ID = "app-users-userRoles-id",
	APP_USERS_USERS = "app-users-users",
	APP_USERS_USERS_ID = "app-users-users-id",
	APP_USERS_ACTORS = "app-users-actors",
	APP_USERS_ACTORS_ID = "app-users-actors-id",
	APP_ADMIN = "app-admin",
	APP_ADMIN_APPLICATIONSTATUS = "app-admin-applicationStatus",
	APP_ADMIN_APPLICATIONVERSION = "app-admin-applicationVersion",
	APP_ADMIN_LOGS = "app-admin-logs",
	APP_ADMIN_LOGS_API = "app-admin-logs-api",
	APP_ADMIN_LOGS_STARTUPLOGS = "app-admin-logs-startupLogs",
	APP_ADMIN_LOGS_STARTUPLOGS_ID = "app-admin-logs-startupLogs-id",
	APP_ADMIN_CRONJOBS = "app-admin-cronJobs",
	APP_ADMIN_CRONJOBS_ID = "app-admin-cronJobs-id",
	APP_ADMIN_EMAILS = "app-admin-emails",
	APP_ADMIN_EMAILS_ID = "app-admin-emails-id",
	APP_ADMIN_FILES = "app-admin-files",
	APP_ADMIN_FILES_ID = "app-admin-files-id",
	APP_ADMIN_HARMONYEVENTS = "app-admin-harmonyEvents",
	APP_ADMIN_HARMONYEVENTS_ID = "app-admin-harmonyEvents-id",
	APP_ADMIN_I18N = "app-admin-i18n",
	APP_ADMIN_I18N_ENTITYATTRIBUTES = "app-admin-i18n-entityAttributes",
	APP_ADMIN_I18N_ENTITYATTRIBUTES_ID = "app-admin-i18n-entityAttributes-id",
	APP_ADMIN_I18N_KEYENTRIES = "app-admin-i18n-keyEntries",
	APP_ADMIN_I18N_KEYENTRIES_ID = "app-admin-i18n-keyEntries-id",
	APP_ADMIN_ERROR = "app-admin-error",
	APP_ADMIN_ERROR_APPLICATIONEXCEPTIONS = "app-admin-error-applicationExceptions",
	APP_ADMIN_ERROR_APPLICATIONEXCEPTIONS_ID = "app-admin-error-applicationExceptions-id",
	APP_ADMIN_ERROR_REPORTUSERERRORS = "app-admin-error-reportUserErrors",
	APP_ADMIN_ERROR_REPORTUSERERRORS_ID = "app-admin-error-reportUserErrors-id",
	APP_ADMIN_NOTIFICATIONS = "app-admin-notifications",
	APP_ADMIN_NOTIFICATIONS_EVENTTYPES = "app-admin-notifications-eventTypes",
	APP_ADMIN_NOTIFICATIONS_EVENTTYPES_ID = "app-admin-notifications-eventTypes-id",
	APP_ADMIN_NOTIFICATIONS_NOTIFICATIONS = "app-admin-notifications-notifications",
	APP_ADMIN_NOTIFICATIONS_NOTIFICATIONS_ID = "app-admin-notifications-notifications-id",
	APP_ADMIN_WEBCONTENT = "app-admin-webContent",
	APP_ADMIN_WEBCONTENT_WEBCONTENTS = "app-admin-webContent-webContents",
	APP_ADMIN_WEBCONTENT_WEBCONTENTS_ID = "app-admin-webContent-webContents-id",
	APP_ADMIN_WEBCONTENT_WEBCONTENTAREAS = "app-admin-webContent-webContentAreas",
	APP_ADMIN_WEBCONTENT_WEBCONTENTAREAS_ID = "app-admin-webContent-webContentAreas-id",
	APP_ADMIN_REGISTRYITEMS = "app-admin-registryItems",
	APP_ADMIN_REGISTRYITEMS_ID = "app-admin-registryItems-id",
	APP_ADMIN_SECUREKEYS = "app-admin-secureKeys",
	APP_ADMIN_SECUREKEYS_ID = "app-admin-secureKeys-id",
	APP_ADMIN_SERVERTASKS = "app-admin-serverTasks",
	APP_ADMIN_SERVERTASKS_ID = "app-admin-serverTasks-id"
}

export enum ECoreBackendPath {
	API_APPRESOURCES_RESOURCENAME_TEMPLATE = "/api/appresources/{resourceName}/template",
	API_APPRESOURCES_RESOURCENAME = "/api/appresources/{resourceName}",
	API_APPVIEWMODELS_VIEWMODELNAME = "/api/appviewmodels/{viewModelName}",
	API_APPVIEWMODELS_VIEWMODELNAME_TEMPLATE = "/api/appviewmodels/{viewModelName}/template",
	API_APPLICATIONSTATUS_RESET = "/api/applicationStatus/reset",
	API_APPLICATIONSTATUS = "/api/applicationStatus",
	API_AUTHENTICATION_ISAUTHENTICATED = "/api/authentication/isAuthenticated",
	API_AUTHENTICATION_ME = "/api/authentication/me",
	API_AUTHENTICATION_USERLOGIN = "/api/authentication/userLogin",
	API_AUTHENTICATION_USERLOGOUT = "/api/authentication/userLogout",
	API_CRONJOBS = "/api/cronJobs",
	API_CRONJOBS_ID = "/api/cronJobs/{id}",
	API_CRONJOBS_UUID_EXECUTE = "/api/cronJobs/{uuid}/execute",
	API_EMAILS_ID = "/api/emails/{id}",
	API_EMAILS = "/api/emails",
	API_EMAILS_UUID_SEND = "/api/emails/{uuid}/send",
	API_FILES_ID = "/api/files/{id}",
	API_FILES_UPLOAD = "/api/files/upload",
	API_FILES_UUID_DOWNLOAD = "/api/files/{uuid}/download",
	API_FILES = "/api/files",
	API_HARMONYEVENTS = "/api/harmonyEvents",
	API_HARMONYEVENTS_ID = "/api/harmonyEvents/{id}",
	API_REGISTRYITEMS = "/api/registryItems",
	API_REGISTRYITEMS_ID = "/api/registryItems/{id}",
	API_SECUREKEYS_ID = "/api/secureKeys/{id}",
	API_SECUREKEYS = "/api/secureKeys",
	API_OPTIONCONTAINERS_CONTAINERNAME_OPTIONS = "/api/optionContainers/{containerName}/options",
	API_OPTIONCONTAINERS = "/api/optionContainers",
	API_SERVERINFO_VERSION_PROJECT = "/api/serverInfo/version/project",
	API_SERVERINFO_BASEAPI = "/api/serverInfo/baseApi",
	API_SERVERINFO_LANGUAGES_ID = "/api/serverInfo/languages/{id}",
	API_SERVERINFO_LOGO = "/api/serverInfo/logo",
	API_SERVERINFO_LANGUAGES_DEFAULT = "/api/serverInfo/languages/default",
	API_SERVERINFO_VERSION_CORE = "/api/serverInfo/version/core",
	API_SERVERINFO_LANGUAGES = "/api/serverInfo/languages",
	API_SERVERTASKS_ID = "/api/servertasks/{id}",
	API_SERVERTASKS = "/api/servertasks",
	API_SERVERTASKS_ID_EXECUTE = "/api/servertasks/{id}/execute",
	API_ERROR_APPLICATIONEXCEPTIONS_ID = "/api/error/applicationExceptions/{id}",
	API_ERROR_APPLICATIONEXCEPTIONS = "/api/error/applicationExceptions",
	API_ERROR_REPORTEDUSERERRORS_REPORT = "/api/error/reportedUserErrors/report",
	API_ERROR_REPORTEDUSERERRORS = "/api/error/reportedUserErrors",
	API_ERROR_REPORTEDUSERERRORS_ID = "/api/error/reportedUserErrors/{id}",
	API_I18NENTITYATTRIBUTE = "/api/i18nEntityAttribute",
	API_I18NENTITYATTRIBUTE_FRONTENDTRANSLATIONS_NEXTUNRESOLVED = "/api/i18nEntityAttribute/frontendTranslations/nextUnresolved",
	API_I18NENTITYATTRIBUTE_IMPORT = "/api/i18nEntityAttribute/import",
	API_I18NENTITYATTRIBUTE_ID = "/api/i18nEntityAttribute/{id}",
	API_I18NENTITYATTRIBUTE_EXPORT = "/api/i18nEntityAttribute/export",
	API_I18NENTITYATTRIBUTE_STATISTICS = "/api/i18nEntityAttribute/statistics",
	API_I18NKEYENTRIES_STATISTICS = "/api/i18nKeyEntries/statistics",
	API_I18NKEYENTRIES_IMPORT = "/api/i18nKeyEntries/import",
	API_I18NKEYENTRIES_ID = "/api/i18nKeyEntries/{id}",
	API_I18NKEYENTRIES_FRONTENDTRANSLATIONS_LANGUAGE = "/api/i18nKeyEntries/frontendTranslations/{language}",
	API_I18NKEYENTRIES_FRONTENDTRANSLATIONS_NEXTUNRESOLVED = "/api/i18nKeyEntries/frontendTranslations/nextUnresolved",
	API_I18NKEYENTRIES = "/api/i18nKeyEntries",
	API_I18NKEYENTRIES_EXPORT = "/api/i18nKeyEntries/export",
	API_STARTUPLOGS_ID = "/api/startupLogs/{id}",
	API_STARTUPLOGS = "/api/startupLogs",
	API_LOGS = "/api/logs",
	API_NOTIFICATIONS_EVENTTYPES_ID = "/api/notifications/eventTypes/{id}",
	API_NOTIFICATIONS_EVENTTYPES = "/api/notifications/eventTypes",
	API_NOTIFICATIONS = "/api/notifications",
	API_NOTIFICATIONS_ID = "/api/notifications/{id}",
	API_NOTIFICATIONS_PERSONAL = "/api/notifications/personal",
	API_NOTIFICATIONS_PERSONAL_INFO = "/api/notifications/personal/info",
	API_NOTIFICATIONS_PERSONAL_ID = "/api/notifications/personal/{id}",
	API_NOTIFICATIONS_PERSONAL_ID_READSTATE = "/api/notifications/personal/{id}/readState",
	API_ACTORS_ID = "/api/actors/{id}",
	API_ACTORS = "/api/actors",
	API_ACTORRIGHTS = "/api/actorRights",
	API_ACTORRIGHTS_ID = "/api/actorRights/{id}",
	API_USERS = "/api/users",
	API_USERS_ID = "/api/users/{id}",
	API_USERS_RESETPASSWORDBYEMAILVM_TOKENVALUE = "/api/users/resetPasswordByEmailVM/{tokenValue}",
	API_USERS_PERSONALACCOUNT_CHANGEPASSWORD = "/api/users/personalAccount/changePassword",
	API_USERS_RESETPASSWORDBYEMAILVM = "/api/users/resetPasswordByEmailVM",
	API_USERS_PERSONALACCOUNT = "/api/users/personalAccount",
	API_USERS_RESETPASSWORD = "/api/users/resetPassword",
	API_USERREGISTRATIONS_CONFIGURATION = "/api/userRegistrations/configuration",
	API_USERREGISTRATIONS_REGISTRATIONBYINVITATIONVM_TOKENVALUE = "/api/userRegistrations/registrationByInvitationVM/{tokenValue}",
	API_USERREGISTRATIONS_INVITATIONS = "/api/userRegistrations/invitations",
	API_USERREGISTRATIONS_CONFIRMEMAIL = "/api/userRegistrations/confirmEmail",
	API_USERREGISTRATIONS_ID = "/api/userRegistrations/{id}",
	API_USERREGISTRATIONS_CONFIRMBYADMIN_USERREGISTRATIONUUID = "/api/userRegistrations/confirmByAdmin/{userRegistrationUUID}",
	API_USERREGISTRATIONS = "/api/userRegistrations",
	API_USERREGISTRATIONS_BYINVITATION = "/api/userRegistrations/byInvitation",
	API_USERREGISTRATIONS_INVITATIONS_TEMPLATE = "/api/userRegistrations/invitations/template",
	API_USERREGISTRATIONS_FROMSCRATCH = "/api/userRegistrations/fromScratch",
	API_USERROLES_ID = "/api/userRoles/{id}",
	API_USERROLES = "/api/userRoles",
	API_WEBCONTENTAREAS_ID = "/api/webContentAreas/{id}",
	API_WEBCONTENTAREAS = "/api/webContentAreas",
	API_WEBCONTENTS_WEBCONTENTUUID_FILES_FILEUUID = "/api/webContents/{webContentUUID}/files/{fileUUID}",
	API_WEBCONTENTS = "/api/webContents",
	API_WEBCONTENTS_CONTENTAREAS_AREA = "/api/webContents/contentAreas/{area}",
	API_WEBCONTENTS_ID = "/api/webContents/{id}",
}
export enum EUserRegistrationWorkflow {
	INVITATION_REGISTRATION_EMAIL = "INVITATION_REGISTRATION_EMAIL",
	REGISTRATION_EMAIL = "REGISTRATION_EMAIL",
	INVITATION_REGISTRATION_EMAIL_CONFIRMATION = "INVITATION_REGISTRATION_EMAIL_CONFIRMATION",
	REGISTRATION_EMAIL_CONFIRMATION = "REGISTRATION_EMAIL_CONFIRMATION",
	NO_REGISTRATION_ALLOWED = "NO_REGISTRATION_ALLOWED"
}

export enum EApplicationErrorLocation {
	FRONTEND = "FRONTEND",
	BACKEND = "BACKEND"
}

export enum EProfile {
	DEV = "DEV",
	PROD = "PROD",
	TEST = "TEST"
}

export enum SortOrder {
	ASC = "ASC",
	DESC = "DESC"
}

export enum EI18nLanguage {
	ENGLISH = "ENGLISH",
	GERMAN = "GERMAN"
}

export enum EUserRegistrationState {
	INVITED = "INVITED",
	WAITING_FOR_EMAIL_CONFIRMATION = "WAITING_FOR_EMAIL_CONFIRMATION",
	WAITING_FOR_ADMIN_CONFIRMATION = "WAITING_FOR_ADMIN_CONFIRMATION",
	USER_CREATED = "USER_CREATED"
}

export enum EI18nCodeLocation {
	BACKEND = "BACKEND",
	FRONTEND = "FRONTEND"
}

export enum EApplicationStatus {
	STARTING = "STARTING",
	TECHNICAL_PROBLEMS = "TECHNICAL_PROBLEMS",
	MAINTENANCE_MODE = "MAINTENANCE_MODE",
	OK = "OK"
}

export enum ECoreWebContentArea {
	CORE_IMPRINT_PAGE = "CORE_IMPRINT_PAGE",
	CORE_PRIVACY_NOTICE_PAGE = "CORE_PRIVACY_NOTICE_PAGE",
	CORE_ABOUT_US_PAGE = "CORE_ABOUT_US_PAGE"
}

export enum EMailState {
	CREATED = "CREATED",
	SENDING = "SENDING",
	SEND = "SEND",
	ERROR_DURING_SENDING = "ERROR_DURING_SENDING"
}

export enum ECoreActorRight {
	CORE_ACTOR_READ_ALL_ACTORS = "CORE_ACTOR_READ_ALL_ACTORS",
	CORE_ACTOR_RIGHTS_CRUD = "CORE_ACTOR_RIGHTS_CRUD",
	CORE_APPLICATION_EXCEPTIONS_CRUD = "CORE_APPLICATION_EXCEPTIONS_CRUD",
	CORE_APPLICATION_STATUS_RESET = "CORE_APPLICATION_STATUS_RESET",
	CORE_APPLICATION_STATUS_SAVE = "CORE_APPLICATION_STATUS_SAVE",
	CORE_APP_ACCESS = "CORE_APP_ACCESS",
	CORE_AUTHENTICATION_OWN_ACTOR = "CORE_AUTHENTICATION_OWN_ACTOR",
	CORE_CRON_JOBS_CRUD = "CORE_CRON_JOBS_CRUD",
	CORE_CRON_JOBS_EXECUTE = "CORE_CRON_JOBS_EXECUTE",
	CORE_EMAIL_CRUD_RIGHTS = "CORE_EMAIL_CRUD_RIGHTS",
	CORE_EMAIL_SEND_BY_ENTITY = "CORE_EMAIL_SEND_BY_ENTITY",
	CORE_ERROR_MESSAGE_FOR_VALIDATION_READ = "CORE_ERROR_MESSAGE_FOR_VALIDATION_READ",
	CORE_ERROR_MESSAGE_DETAIL_READ = "CORE_ERROR_MESSAGE_DETAIL_READ",
	CORE_ERROR_JAVA_STACKTRACE_READ = "CORE_ERROR_JAVA_STACKTRACE_READ",
	CORE_FILES_CRUD = "CORE_FILES_CRUD",
	CORE_HARMONY_EVENT_CRUD = "CORE_HARMONY_EVENT_CRUD",
	CORE_HOME_ACCESS = "CORE_HOME_ACCESS",
	CORE_I18N_ENTITY_ATTRIBUTE_CRUD = "CORE_I18N_ENTITY_ATTRIBUTE_CRUD",
	CORE_I18N_KEY_ENTRY_CRUD = "CORE_I18N_KEY_ENTRY_CRUD",
	CORE_LOAD_SELECTABLE_RIGHTS_FOR_OPTION_CONTAINER = "CORE_LOAD_SELECTABLE_RIGHTS_FOR_OPTION_CONTAINER",
	CORE_LOAD_SELECTABLE_ROLES_FOR_OPTION_CONTAINER = "CORE_LOAD_SELECTABLE_ROLES_FOR_OPTION_CONTAINER",
	CORE_LOGS_READ = "CORE_LOGS_READ",
	CORE_NOTIFICATIONS = "CORE_NOTIFICATIONS",
	CORE_NOTIFICATION_EVENT_TYPE_CRUD = "CORE_NOTIFICATION_EVENT_TYPE_CRUD",
	CORE_PERSONAL_NOTIFICATIONS = "CORE_PERSONAL_NOTIFICATIONS",
	CORE_REGISTRATION_ADMIN_CONFIRMATION = "CORE_REGISTRATION_ADMIN_CONFIRMATION",
	CORE_REGISTRATION_CRUD = "CORE_REGISTRATION_CRUD",
	CORE_REGISTRATION_SEND_INVITATIONS = "CORE_REGISTRATION_SEND_INVITATIONS",
	CORE_REGISTRY_CRUD = "CORE_REGISTRY_CRUD",
	CORE_REPORT_USER_ERRORS_CRUD = "CORE_REPORT_USER_ERRORS_CRUD",
	CORE_SERVER_TASKS_CRUD = "CORE_SERVER_TASKS_CRUD",
	CORE_SERVER_TASKS_EXECUTE = "CORE_SERVER_TASKS_EXECUTE",
	CORE_SERVER_INFO_READ = "CORE_SERVER_INFO_READ",
	CORE_SERVER_STATUS_NOT_OK_USER_HAS_ACCESS = "CORE_SERVER_STATUS_NOT_OK_USER_HAS_ACCESS",
	CORE_STARTUP_LOGS_CRUD = "CORE_STARTUP_LOGS_CRUD",
	CORE_USERS_CRUD = "CORE_USERS_CRUD",
	CORE_USER_PERSONAL_ACCOUNT_CRUD = "CORE_USER_PERSONAL_ACCOUNT_CRUD",
	CORE_USER_ROLES_CRUD = "CORE_USER_ROLES_CRUD",
	CORE_SECURE_KEYS_CRUD = "CORE_SECURE_KEYS_CRUD",
	CORE_WEB_CONTENT_CRUD = "CORE_WEB_CONTENT_CRUD",
	CORE_WEB_CONTENT_AREA_CRUD = "CORE_WEB_CONTENT_AREA_CRUD"
}

export enum BoxAlignment {
	HORIZONTAL = "HORIZONTAL",
	VERTICAL = "VERTICAL"
}

export interface ApiJavaExceptionInstance  {
	message?: String,
	stackTraceText?: String
}

export interface TitleWebContent extends AbstractWebContent {
	title?: String
}

export interface UserPersonalAccountDeleteVM  {
	password?: String
}

export interface AbstractWebContent  {
	type?: String
}

export interface I18nFrontendTranslationEntry  {
	classId?: String,
	keyId?: String,
	value?: String
}

export interface ServerTaskDto extends AbstractResourceDto {
	taskId?: String,
	taskName?: String,
	lastExecution?: String,
	isRequired?: Boolean
}

export interface SecureKeyDto extends AbstractResourceDto {
	name?: String,
	key?: String
}

export interface AuthenticatedUserDto  {
	username?: String,
	firstname?: String,
	lastname?: String,
	effectiveRights?: String[]
}

export interface AbstractWebContentWithChildren extends AbstractWebContent {
	children?: AbstractWebContent[]
}

export interface RestSort  {
	name?: String,
	order?: SortOrder
}

export interface ReportedUserErrorDto extends AbstractResourceDto {
	page?: String,
	description?: String,
	attachments?: FileWebData[]
}

export interface ValidationFieldErrorDto  {
	fieldPath?: String,
	errorMessages?: ValidationErrorMessageDto[]
}

export interface ApiLinkResourceSpecification extends ValidInputSpecification {
	apiLink?: ApiLink
}

export interface HarmonyEventDto extends AbstractResourceDto {
	createdBy?: String,
	createdAt?: String,
	javaType?: String,
	payload?: String
}

export interface SearchFilterSelectionOption  {
	label?: String,
	value?: String
}

export interface ReportUserBugRequest  {
	page?: String,
	description?: String,
	attachments?: FileWebData[]
}

export interface AccessRuleConfigJson  {
	isPublic?: Boolean,
	isAndConnected?: Boolean,
	rights?: String[]
}

export interface UserLoginVM  {
	username?: String,
	password?: String
}

export interface SimpleFieldTypeSchema  {
	simpleType?: String,
	javaType?: String,
	jsonType?: String,
	validInputSpecification?: ValidInputSpecification
}

export interface BoxWebContent extends AbstractWebContentWithChildren {
	children?: AbstractWebContent[],
	align?: BoxAlignment
}

export interface VersionDetail  {
	build?: Number,
	version?: String
}

export interface ApiLinkPlaceholder  {
	type?: String,
	name?: String,
	isOptional?: Boolean
}

export interface I18nTranslationStatistic  {
	countOfAllItems?: Number,
	countOfTranslatedItems?: Number,
	countOfNotTranslatedItems?: Number
}

export interface CrudResourceInfoWithOverview extends CrudResourceInfoDto {
	resourceOverviewTypeSchema?: ResourceOverviewTypeSchema
}

export interface ViewModel  {
}

export interface ImageWebContent extends AbstractWebContent {
	imageTitle?: String,
	fileWebData?: FileWebData,
	width?: Number,
	height?: Number,
	showImageTitle?: Boolean
}

export interface ComplexTypeSchema extends SimpleFieldTypeSchema {
	fields?: Map<String, SimpleFieldTypeSchema>
}

export interface UserLogoutRequest  {
	token?: String
}

export interface ProjectMetaData  {
	projectShortName?: String,
	projectLongName?: String
}

export interface ResourceOverviewTypeSchema  {
	fields?: Map<String, SimpleFieldTypeSchema>,
	sortOptions?: String[],
	filters?: RestFilterInfo[]
}

export interface UserAccessDto extends BaseDto {
	isAdmin?: Boolean,
	mainRole?: ApiResource<any>,
	roles?: ApiResource<any>[],
	additionalRights?: ApiResource<any>[]
}

export interface UserPersonalAccountResetPasswordByEmailVM  {
	email?: String,
	tokenValue?: String,
	password?: String,
	passwordAgain?: String
}

export interface Point2D  {
	x?: Number,
	y?: Number
}

export interface FrontendProjectConfiguration  {
	projectMeta?: ProjectMetaData,
	routerPages?: RouterPagesRoot
}

export interface CronJobDto extends AbstractResourceDto {
	label?: String,
	description?: String,
	javaClass?: String,
	lastExecutedAt?: String,
	isActivated?: Boolean,
	isCurrentlyRunning?: Boolean,
	cronTrigger?: String
}

export interface FrontendRuntimeError extends BaseDto {
	message?: String,
	url?: String,
	line?: Number,
	col?: Number,
	error?: String
}

export interface UserRegistrationVM  {
	email?: String,
	firstname?: String,
	lastname?: String,
	password?: String,
	passwordAgain?: String
}

export interface EmailDto extends AbstractResourceDto {
	state?: EMailState,
	fromEmail?: String,
	createdAt?: String,
	toEmail?: String,
	subject?: String,
	htmlMessage?: String,
	eventLog?: String,
	lastSending?: String,
	attachments?: ApiResource<FileDto>[]
}

export interface GeneralApiResource<T> extends ApiObject<T> {
	resourceLinks?: ResourceLinks
}

export interface RegistrationConfiguration extends BaseDto {
	isInvitationAllowed?: Boolean,
	isRegistrationByInvitationAllowed?: Boolean,
	isRegistrationFromScratchAllowed?: Boolean,
	isEmailConfirmationAllowed?: Boolean,
	isAdminConfirmationRequired?: Boolean
}

export interface ValidInputSpecification  {
	isReadOnly?: Boolean,
	type?: String
}

export interface I18nKeyEntryDto extends AbstractResourceDto {
	classId?: String,
	key?: String,
	placeholders?: String,
	codeLocation?: EI18nCodeLocation,
	codeLines?: String,
	description?: String,
	translationEntries?: I18nTranslationDto[]
}

export interface ApiLink  {
	requestMethod?: String,
	link?: String,
	placeholders?: ApiLinkPlaceholder[]
}

export interface ActorNotificationDto extends AbstractResourceDto {
	sendAt?: String,
	caption?: String,
	read?: Boolean,
	textMessage?: String,
	recipient?: String,
	payload?: String,
	eventType?: ActorNotificationEventTypeDto
}

export interface UserRoleDto extends AbstractResourceDto {
	label?: String,
	description?: String,
	includedRights?: ApiResource<ActorRightDto>[]
}

export interface I18nTranslationDto extends BaseDto {
	language?: EI18nLanguage,
	translation?: String
}

export interface TextWebContent extends AbstractWebContent {
	text?: String
}

export interface RouterPagesRoot  {
	pages?: PageJson[],
	navigationTree?: NavigationTreeJson
}

export interface FileWebData  {
	id?: String,
	name?: String,
	type?: String,
	size?: Number,
	lastModified?: String,
	base64Content?: String,
	downloadLink?: ApiLink,
	simpleDownloadLink?: String
}

export interface FileDto extends AbstractResourceDto {
	fileName?: String,
	isTemp?: Boolean
}

export interface FrontendComponentJson  {
	componentPath?: String,
	isCoreComponent?: Boolean
}

export interface WebContentAreaDto extends AbstractResourceDto {
	uniqueName?: String,
	label?: String,
	description?: String,
	onlyOneContentInstanceAllowed?: Boolean,
	requiredReadRight?: ApiResource<any>,
	requiredWriteRight?: ApiResource<any>
}

export interface SendUserRegistrationInvitationVM  {
	email?: String,
	message?: String
}

export interface LoginResult  {
	type?: String,
	token?: String,
	tokenHeaderField?: String
}

export interface ResourceLinks  {
	self?: ApiLink,
	update?: ApiLink,
	delete?: ApiLink
}

export interface I18nImportResult  {
	importedElements?: Number,
	ignoredElements?: Number
}

export interface ValidationResultDto  {
	fields?: ValidationFieldErrorDto[]
}

export interface ApiError  {
	status?: Number,
	statusText?: String,
	message?: String,
	type?: String,
	data?: any
}

export interface UserRegistrationDto extends AbstractResourceDto {
	email?: String,
	state?: EUserRegistrationState,
	workflow?: EUserRegistrationWorkflow,
	stateData?: String
}

export interface LabelValueObject  {
	label?: String,
	value?: String
}

export interface ResourcePageableOptions  {
	isUnPagedRequestAllowed?: Boolean,
	suggestedPageOptions?: Number[],
	highestPageSize?: Number
}

export interface ApplicationStartupLogDto extends AbstractResourceDto {
	startedAt?: String,
	durationInMillis?: Number,
	durationText?: String
}

export interface SearchResult  {
	totalResults?: Number,
	page?: Number,
	isPaged?: Boolean,
	size?: Number,
	sortedBy?: String[],
	data?: GeneralApiResource<Map<String, any>>[],
	primaryKeyName?: String
}

export interface ApplicationStatusVM  {
	status?: EApplicationStatus,
	userMessage?: String,
	technicalMessage?: String
}

export interface ApiResource<R> extends GeneralApiResource<R> {
}

export interface EnumInputSpecification extends ValidInputSpecification {
	values?: LabelValueObject[]
}

export interface ViewModelInfoDto  {
	viewModelName?: String,
	schema?: ComplexTypeSchema,
	loadLink?: ApiLink,
	saveLink?: ApiLink
}

export interface ActorRightDto extends AbstractResourceDto {
	label?: String,
	description?: String,
	isAllowedForSystemActor?: Boolean,
	isAllowedForUnknownPublicActor?: Boolean
}

export interface RestFilterInfo  {
	filterName?: String,
	type?: FilterType<any>,
	apiLinkForOptions?: ApiLink,
	options?: SearchFilterSelectionOption[],
	filterType?: String
}

export interface LanguageInfo  {
	id?: String,
	label?: String,
	dateFormatTemplate?: String
}

export interface AbstractResourceDto extends BaseDto {
	id?: String
}

export interface ApplicationExceptionDto extends AbstractResourceDto {
	timestamp?: String,
	exceptionType?: String,
	message?: String,
	codeLocation?: EApplicationErrorLocation,
	description?: String,
	stacktrace?: String,
	log?: String
}

export interface UserPersonalAccountForgotPasswordVM  {
	email?: String
}

export interface ServerApiBaseInfo  {
	serverIsAvailable?: Boolean,
	apiBasePath?: String,
	profile?: EProfile
}

export interface UserPersonalAccountVM  {
	email?: String,
	firstname?: String,
	lastname?: String
}

export interface NavigationTreeJson  {
	items?: NavigationItemJson[]
}

export interface PageJson  {
	id?: String,
	isCorePage?: Boolean,
	path?: String,
	englishLabel?: String,
	frontendComponent?: FrontendComponentJson,
	accessRuleConfigJson?: AccessRuleConfigJson
}

export interface FilterType<T>  {
}

export interface CrudResourceInfoDto extends CrudResourceInfoSimpleDto {
	schema?: ComplexTypeSchema,
	getAllLink?: ApiLink,
	getByIdLink?: ApiLink,
	createNewLink?: ApiLink,
	updateLink?: ApiLink,
	deleteLink?: ApiLink,
	getTemplateLink?: ApiLink,
	pageableOptions?: ResourcePageableOptions
}

export interface ApiErrorWithJavaExceptionInstance extends ApiError {
	javaException?: ApiJavaExceptionInstance
}

export interface TextInputSpecification extends ValidInputSpecification {
}

export interface ActorDto extends AbstractResourceDto {
	type?: String,
	uniqueName?: String,
	userId?: String
}

export interface ActorPersonalNotificationInfoDto extends BaseDto {
	totalUnread?: Number
}

export interface ApiObject<T>  {
	primaryKey?: any,
	data?: T
}

export interface BaseDto  {
	dtoType?: String
}

export interface WebContentDto extends AbstractResourceDto {
	label?: String,
	content?: AbstractWebContent,
	area?: ApiResource<any>
}

export interface ResponseResource<D>  {
	data?: ApiResource<D>,
	dataType?: String
}

export interface UserDto extends AbstractResourceDto {
	label?: String,
	email?: String,
	firstname?: String,
	lastname?: String,
	userAccessConfig?: UserAccessDto
}

export interface RegistryItemDto extends AbstractResourceDto {
	uniqueName?: String,
	label?: String,
	description?: String,
	value?: any,
	javaType?: String,
	javaSpecificationType?: String,
	definedSelectableOptions?: any[]
}

export interface UserRegistrationWithInvitationVM extends UserRegistrationVM {
	email?: String,
	tokenValue?: String
}

export interface ActorNotificationEventTypeDto extends AbstractResourceDto {
	label?: String,
	description?: String,
	isActive?: Boolean
}

export interface DefaultInputSpecification extends ValidInputSpecification {
}

export interface CrudResourceInfoSimpleDto  {
	name?: String
}

export interface NavigationItemJson  {
	pageId?: String,
	englishLabel?: String,
	link?: String,
	icon?: String,
	accessRule?: AccessRuleConfigJson,
	children?: NavigationItemJson[]
}

export interface I18nFrontendTranslation  {
	language?: EI18nLanguage,
	translationEntries?: I18nFrontendTranslationEntry[]
}

export interface ConfirmEmailByTokenBody  {
	token?: String
}

export interface UserPersonalAccountChangePasswordVM  {
	oldPassword?: String,
	newPassword?: String,
	newPasswordAgain?: String
}

export interface ValidationErrorMessageDto  {
	validationName?: String,
	message?: String,
	stackTraceText?: String
}

export interface I18nEntityAttributeDto extends AbstractResourceDto {
	entityClass?: String,
	attribute?: String,
	values?: I18nTranslationDto[]
}

export class RestRequestParams {
	queryParameters?: Map<String, any>

	constructor() {
		this.queryParameters = new Map<String, any>();
	}

	static create():RestRequestParams {
		return new RestRequestParams()
	}

	withPage(page: number): RestRequestParams {
		this.addQueryParameter("page", page)
		return this
	}

	withIsPaged(isPaged: boolean): RestRequestParams {
		this.addQueryParameter("isPaged", isPaged)
		return this;
	}

	withSize(size: number): RestRequestParams {
		this.addQueryParameter("size", size)
		return this;
	}

	withAttributes(attributes: String[]): RestRequestParams {
		this.addQueryParameter("attributes", attributes.join(","))
		return this;
	}

	withSorts(sorts: RestSort[]): RestRequestParams {
		const sortFragments:string[] = []
		for(let sort of sorts) {
			sortFragments.push(sort.name + ":" + sort.order)
		}
		if(sortFragments.length > 0) {
			this.addQueryParameter("sort", sortFragments.join(","))
		}
		return this;
	}

	addQueryParameter(key: string, value: any): RestRequestParams {
		this.queryParameters!.set(key, value);
		return this;
	}

	toQueryString(): string {
		const queryFragments:string[] = []
		for (let entry of Array.from(this.queryParameters!.entries())) {
			let key = entry[0];
			let value = entry[1];
			queryFragments.push(key + "=" + value)
		}

		return queryFragments.length > 0 ? "?" + queryFragments.join("&") : ""
	}
}

// ################ Model declaration end ################


export interface ApiResolver {
	resolveRequest(apiMethod: string, apiPath: string, body?: any): Promise<any>,
	resolveUrlForRestRequestParams(baseUrl: string, restRequestParams: RestRequestParams):string
}


// ################ API Method declaration start ################

export default function(apiResolver: ApiResolver) {
	return {
		api() {
			return {
				getActorApi() {
					return {

						async getEntryById(id: String):Promise<AxiosResponse<ResponseResource<ActorDto>, any>> {
							const response = await apiResolver.resolveRequest("GET", `/api/actors/${id}`, undefined)
							return <AxiosResponse<ResponseResource<ActorDto>, any>> response
						},
						async getAllEntries(restRequestParams: RestRequestParams):Promise<AxiosResponse<SearchResult, any>> {
							const response = await apiResolver.resolveRequest("GET", apiResolver.resolveUrlForRestRequestParams("/api/actors", restRequestParams), undefined)
							return <AxiosResponse<SearchResult, any>> response
						},
					}
				},

				getActorRightApi() {
					return {

						async getAllEntries(restRequestParams: RestRequestParams):Promise<AxiosResponse<SearchResult, any>> {
							const response = await apiResolver.resolveRequest("GET", apiResolver.resolveUrlForRestRequestParams("/api/actorRights", restRequestParams), undefined)
							return <AxiosResponse<SearchResult, any>> response
						},
						async getEntryById(id: String):Promise<AxiosResponse<ResponseResource<ActorRightDto>, any>> {
							const response = await apiResolver.resolveRequest("GET", `/api/actorRights/${id}`, undefined)
							return <AxiosResponse<ResponseResource<ActorRightDto>, any>> response
						},
						async updateEntry(id: String, body: ActorRightDto):Promise<AxiosResponse<ResponseResource<ActorRightDto>, any>> {
							const response = await apiResolver.resolveRequest("PUT", `/api/actorRights/${id}`, body)
							return <AxiosResponse<ResponseResource<ActorRightDto>, any>> response
						},
					}
				},

				getAppResourceApi() {
					return {

						async getInitialResourceTemplate(resourceName: String):Promise<AxiosResponse<AbstractResourceDto, any>> {
							const response = await apiResolver.resolveRequest("GET", `/api/appresources/${resourceName}/template`, undefined)
							return <AxiosResponse<AbstractResourceDto, any>> response
						},
						async getResourceByName(resourceName: String):Promise<AxiosResponse<CrudResourceInfoDto, any>> {
							const response = await apiResolver.resolveRequest("GET", `/api/appresources/${resourceName}`, undefined)
							return <AxiosResponse<CrudResourceInfoDto, any>> response
						},
					}
				},

				getAppViewModelApi() {
					return {

						async getViewModelByName(viewModelName: String):Promise<AxiosResponse<ViewModelInfoDto, any>> {
							const response = await apiResolver.resolveRequest("GET", `/api/appviewmodels/${viewModelName}`, undefined)
							return <AxiosResponse<ViewModelInfoDto, any>> response
						},
						async getInitialViewModelTemplate(viewModelName: String):Promise<AxiosResponse<ViewModel, any>> {
							const response = await apiResolver.resolveRequest("GET", `/api/appviewmodels/${viewModelName}/template`, undefined)
							return <AxiosResponse<ViewModel, any>> response
						},
					}
				},

				getApplicationExceptionApi() {
					return {

						async updateEntry(id: String, body: ApplicationExceptionDto):Promise<AxiosResponse<ResponseResource<ApplicationExceptionDto>, any>> {
							const response = await apiResolver.resolveRequest("PUT", `/api/error/applicationExceptions/${id}`, body)
							return <AxiosResponse<ResponseResource<ApplicationExceptionDto>, any>> response
						},
						async deleteAllExceptions():Promise<AxiosResponse<any, any>> {
							const response = await apiResolver.resolveRequest("DELETE", "/api/error/applicationExceptions", undefined)
							return <AxiosResponse<any, any>> response
						},
						async getAllEntries(restRequestParams: RestRequestParams):Promise<AxiosResponse<SearchResult, any>> {
							const response = await apiResolver.resolveRequest("GET", apiResolver.resolveUrlForRestRequestParams("/api/error/applicationExceptions", restRequestParams), undefined)
							return <AxiosResponse<SearchResult, any>> response
						},
						async reportFrontendException(body: FrontendRuntimeError):Promise<AxiosResponse<any, any>> {
							const response = await apiResolver.resolveRequest("POST", "/api/error/applicationExceptions", body)
							return <AxiosResponse<any, any>> response
						},
						async deleteEntry(id: String):Promise<AxiosResponse<any, any>> {
							const response = await apiResolver.resolveRequest("DELETE", `/api/error/applicationExceptions/${id}`, undefined)
							return <AxiosResponse<any, any>> response
						},
						async getEntryById(id: String):Promise<AxiosResponse<ResponseResource<ApplicationExceptionDto>, any>> {
							const response = await apiResolver.resolveRequest("GET", `/api/error/applicationExceptions/${id}`, undefined)
							return <AxiosResponse<ResponseResource<ApplicationExceptionDto>, any>> response
						},
					}
				},

				getApplicationStartupLogApi() {
					return {

						async getEntryById(id: String):Promise<AxiosResponse<ResponseResource<ApplicationStartupLogDto>, any>> {
							const response = await apiResolver.resolveRequest("GET", `/api/startupLogs/${id}`, undefined)
							return <AxiosResponse<ResponseResource<ApplicationStartupLogDto>, any>> response
						},
						async getAllEntries(restRequestParams: RestRequestParams):Promise<AxiosResponse<SearchResult, any>> {
							const response = await apiResolver.resolveRequest("GET", apiResolver.resolveUrlForRestRequestParams("/api/startupLogs", restRequestParams), undefined)
							return <AxiosResponse<SearchResult, any>> response
						},
					}
				},

				getApplicationStatusApi() {
					return {

						async resetStatus():Promise<AxiosResponse<any, any>> {
							const response = await apiResolver.resolveRequest("POST", "/api/applicationStatus/reset", undefined)
							return <AxiosResponse<any, any>> response
						},
						async getCurrentStatus():Promise<AxiosResponse<ApplicationStatusVM, any>> {
							const response = await apiResolver.resolveRequest("GET", "/api/applicationStatus", undefined)
							return <AxiosResponse<ApplicationStatusVM, any>> response
						},
						async saveCurrentStatus(body: ApplicationStatusVM):Promise<AxiosResponse<any, any>> {
							const response = await apiResolver.resolveRequest("PUT", "/api/applicationStatus", body)
							return <AxiosResponse<any, any>> response
						},
					}
				},

				getAuthenticationApi() {
					return {

						async isAuthenticated():Promise<AxiosResponse<Boolean, any>> {
							const response = await apiResolver.resolveRequest("GET", "/api/authentication/isAuthenticated", undefined)
							return <AxiosResponse<Boolean, any>> response
						},
						async getOwnUser():Promise<AxiosResponse<AuthenticatedUserDto, any>> {
							const response = await apiResolver.resolveRequest("GET", "/api/authentication/me", undefined)
							return <AxiosResponse<AuthenticatedUserDto, any>> response
						},
						async userLogin(body: UserLoginVM):Promise<AxiosResponse<LoginResult, any>> {
							const response = await apiResolver.resolveRequest("POST", "/api/authentication/userLogin", body)
							return <AxiosResponse<LoginResult, any>> response
						},
						async userLogout(body: UserLogoutRequest):Promise<AxiosResponse<any, any>> {
							const response = await apiResolver.resolveRequest("POST", "/api/authentication/userLogout", body)
							return <AxiosResponse<any, any>> response
						},
					}
				},

				getCronJobApi() {
					return {

						async getAllEntries(restRequestParams: RestRequestParams):Promise<AxiosResponse<SearchResult, any>> {
							const response = await apiResolver.resolveRequest("GET", apiResolver.resolveUrlForRestRequestParams("/api/cronJobs", restRequestParams), undefined)
							return <AxiosResponse<SearchResult, any>> response
						},
						async updateEntry(id: String, body: CronJobDto):Promise<AxiosResponse<ResponseResource<CronJobDto>, any>> {
							const response = await apiResolver.resolveRequest("PUT", `/api/cronJobs/${id}`, body)
							return <AxiosResponse<ResponseResource<CronJobDto>, any>> response
						},
						async execute(uuid: String):Promise<AxiosResponse<any, any>> {
							const response = await apiResolver.resolveRequest("POST", `/api/cronJobs/${uuid}/execute`, undefined)
							return <AxiosResponse<any, any>> response
						},
						async getEntryById(id: String):Promise<AxiosResponse<ResponseResource<CronJobDto>, any>> {
							const response = await apiResolver.resolveRequest("GET", `/api/cronJobs/${id}`, undefined)
							return <AxiosResponse<ResponseResource<CronJobDto>, any>> response
						},
					}
				},

				getEmailApi() {
					return {

						async deleteEntry(id: String):Promise<AxiosResponse<any, any>> {
							const response = await apiResolver.resolveRequest("DELETE", `/api/emails/${id}`, undefined)
							return <AxiosResponse<any, any>> response
						},
						async getAllEntries(restRequestParams: RestRequestParams):Promise<AxiosResponse<SearchResult, any>> {
							const response = await apiResolver.resolveRequest("GET", apiResolver.resolveUrlForRestRequestParams("/api/emails", restRequestParams), undefined)
							return <AxiosResponse<SearchResult, any>> response
						},
						async getEntryById(id: String):Promise<AxiosResponse<ResponseResource<EmailDto>, any>> {
							const response = await apiResolver.resolveRequest("GET", `/api/emails/${id}`, undefined)
							return <AxiosResponse<ResponseResource<EmailDto>, any>> response
						},
						async updateEntry(id: String, body: EmailDto):Promise<AxiosResponse<ResponseResource<EmailDto>, any>> {
							const response = await apiResolver.resolveRequest("PUT", `/api/emails/${id}`, body)
							return <AxiosResponse<ResponseResource<EmailDto>, any>> response
						},
						async sendEmail(uuid: String):Promise<AxiosResponse<any, any>> {
							const response = await apiResolver.resolveRequest("POST", `/api/emails/${uuid}/send`, undefined)
							return <AxiosResponse<any, any>> response
						},
						async createNewEntry(body: EmailDto):Promise<AxiosResponse<ResponseResource<EmailDto>, any>> {
							const response = await apiResolver.resolveRequest("POST", "/api/emails", body)
							return <AxiosResponse<ResponseResource<EmailDto>, any>> response
						},
					}
				},

				getFileApi() {
					return {

						async deleteEntry(id: String):Promise<AxiosResponse<any, any>> {
							const response = await apiResolver.resolveRequest("DELETE", `/api/files/${id}`, undefined)
							return <AxiosResponse<any, any>> response
						},
						async getEntryById(id: String):Promise<AxiosResponse<ResponseResource<FileDto>, any>> {
							const response = await apiResolver.resolveRequest("GET", `/api/files/${id}`, undefined)
							return <AxiosResponse<ResponseResource<FileDto>, any>> response
						},
						async updateEntry(id: String, body: FileDto):Promise<AxiosResponse<ResponseResource<FileDto>, any>> {
							const response = await apiResolver.resolveRequest("PUT", `/api/files/${id}`, body)
							return <AxiosResponse<ResponseResource<FileDto>, any>> response
						},
						async handleFileUpload(body: FileWebData[]):Promise<AxiosResponse<ApiResource<FileDto>[], any>> {
							const response = await apiResolver.resolveRequest("POST", "/api/files/upload", body)
							return <AxiosResponse<ApiResource<FileDto>[], any>> response
						},
						async getAllEntries(restRequestParams: RestRequestParams):Promise<AxiosResponse<SearchResult, any>> {
							const response = await apiResolver.resolveRequest("GET", apiResolver.resolveUrlForRestRequestParams("/api/files", restRequestParams), undefined)
							return <AxiosResponse<SearchResult, any>> response
						},
					}
				},

				getHarmonyEventApi() {
					return {

						async getAllEntries(restRequestParams: RestRequestParams):Promise<AxiosResponse<SearchResult, any>> {
							const response = await apiResolver.resolveRequest("GET", apiResolver.resolveUrlForRestRequestParams("/api/harmonyEvents", restRequestParams), undefined)
							return <AxiosResponse<SearchResult, any>> response
						},
						async getEntryById(id: String):Promise<AxiosResponse<ResponseResource<HarmonyEventDto>, any>> {
							const response = await apiResolver.resolveRequest("GET", `/api/harmonyEvents/${id}`, undefined)
							return <AxiosResponse<ResponseResource<HarmonyEventDto>, any>> response
						},
					}
				},

				getI18nEntityAttributeApi() {
					return {

						async getAllEntries(restRequestParams: RestRequestParams):Promise<AxiosResponse<SearchResult, any>> {
							const response = await apiResolver.resolveRequest("GET", apiResolver.resolveUrlForRestRequestParams("/api/i18nEntityAttribute", restRequestParams), undefined)
							return <AxiosResponse<SearchResult, any>> response
						},
						async getIdOfNextEntryToBeResolved():Promise<AxiosResponse<String, any>> {
							const response = await apiResolver.resolveRequest("GET", "/api/i18nEntityAttribute/frontendTranslations/nextUnresolved", undefined)
							return <AxiosResponse<String, any>> response
						},
						async importKeyEntryData(body: FileWebData):Promise<AxiosResponse<I18nImportResult, any>> {
							const response = await apiResolver.resolveRequest("POST", "/api/i18nEntityAttribute/import", body)
							return <AxiosResponse<I18nImportResult, any>> response
						},
						async getEntryById(id: String):Promise<AxiosResponse<ResponseResource<I18nEntityAttributeDto>, any>> {
							const response = await apiResolver.resolveRequest("GET", `/api/i18nEntityAttribute/${id}`, undefined)
							return <AxiosResponse<ResponseResource<I18nEntityAttributeDto>, any>> response
						},
						async updateEntry(id: String, body: I18nEntityAttributeDto):Promise<AxiosResponse<ResponseResource<I18nEntityAttributeDto>, any>> {
							const response = await apiResolver.resolveRequest("PUT", `/api/i18nEntityAttribute/${id}`, body)
							return <AxiosResponse<ResponseResource<I18nEntityAttributeDto>, any>> response
						},
						async getStatistics():Promise<AxiosResponse<I18nTranslationStatistic, any>> {
							const response = await apiResolver.resolveRequest("GET", "/api/i18nEntityAttribute/statistics", undefined)
							return <AxiosResponse<I18nTranslationStatistic, any>> response
						},
					}
				},

				getI18nKeyEntryApi() {
					return {

						async getStatistics():Promise<AxiosResponse<I18nTranslationStatistic, any>> {
							const response = await apiResolver.resolveRequest("GET", "/api/i18nKeyEntries/statistics", undefined)
							return <AxiosResponse<I18nTranslationStatistic, any>> response
						},
						async importKeyEntryData(body: FileWebData):Promise<AxiosResponse<I18nImportResult, any>> {
							const response = await apiResolver.resolveRequest("POST", "/api/i18nKeyEntries/import", body)
							return <AxiosResponse<I18nImportResult, any>> response
						},
						async getEntryById(id: String):Promise<AxiosResponse<ResponseResource<I18nKeyEntryDto>, any>> {
							const response = await apiResolver.resolveRequest("GET", `/api/i18nKeyEntries/${id}`, undefined)
							return <AxiosResponse<ResponseResource<I18nKeyEntryDto>, any>> response
						},
						async updateEntry(id: String, body: I18nKeyEntryDto):Promise<AxiosResponse<ResponseResource<I18nKeyEntryDto>, any>> {
							const response = await apiResolver.resolveRequest("PUT", `/api/i18nKeyEntries/${id}`, body)
							return <AxiosResponse<ResponseResource<I18nKeyEntryDto>, any>> response
						},
						async getFrontendTranslation(language: String):Promise<AxiosResponse<I18nFrontendTranslation, any>> {
							const response = await apiResolver.resolveRequest("GET", `/api/i18nKeyEntries/frontendTranslations/${language}`, undefined)
							return <AxiosResponse<I18nFrontendTranslation, any>> response
						},
						async getIdOfNextEntryToBeResolved():Promise<AxiosResponse<String, any>> {
							const response = await apiResolver.resolveRequest("GET", "/api/i18nKeyEntries/frontendTranslations/nextUnresolved", undefined)
							return <AxiosResponse<String, any>> response
						},
						async getAllEntries(restRequestParams: RestRequestParams):Promise<AxiosResponse<SearchResult, any>> {
							const response = await apiResolver.resolveRequest("GET", apiResolver.resolveUrlForRestRequestParams("/api/i18nKeyEntries", restRequestParams), undefined)
							return <AxiosResponse<SearchResult, any>> response
						},
					}
				},

				getLogApi() {
					return {

						async getAllLogs():Promise<AxiosResponse<String[], any>> {
							const response = await apiResolver.resolveRequest("GET", "/api/logs", undefined)
							return <AxiosResponse<String[], any>> response
						},
					}
				},

				getNotificationEventTypeApi() {
					return {

						async getEntryById(id: String):Promise<AxiosResponse<ResponseResource<ActorNotificationEventTypeDto>, any>> {
							const response = await apiResolver.resolveRequest("GET", `/api/notifications/eventTypes/${id}`, undefined)
							return <AxiosResponse<ResponseResource<ActorNotificationEventTypeDto>, any>> response
						},
						async updateEntry(id: String, body: ActorNotificationEventTypeDto):Promise<AxiosResponse<ResponseResource<ActorNotificationEventTypeDto>, any>> {
							const response = await apiResolver.resolveRequest("PUT", `/api/notifications/eventTypes/${id}`, body)
							return <AxiosResponse<ResponseResource<ActorNotificationEventTypeDto>, any>> response
						},
						async getAllEntries(restRequestParams: RestRequestParams):Promise<AxiosResponse<SearchResult, any>> {
							const response = await apiResolver.resolveRequest("GET", apiResolver.resolveUrlForRestRequestParams("/api/notifications/eventTypes", restRequestParams), undefined)
							return <AxiosResponse<SearchResult, any>> response
						},
					}
				},

				getNotificationsApi() {
					return {

						async getAllEntries(restRequestParams: RestRequestParams):Promise<AxiosResponse<SearchResult, any>> {
							const response = await apiResolver.resolveRequest("GET", apiResolver.resolveUrlForRestRequestParams("/api/notifications", restRequestParams), undefined)
							return <AxiosResponse<SearchResult, any>> response
						},
						async deleteEntry(id: String):Promise<AxiosResponse<any, any>> {
							const response = await apiResolver.resolveRequest("DELETE", `/api/notifications/${id}`, undefined)
							return <AxiosResponse<any, any>> response
						},
						async getEntryById(id: String):Promise<AxiosResponse<ResponseResource<ActorNotificationDto>, any>> {
							const response = await apiResolver.resolveRequest("GET", `/api/notifications/${id}`, undefined)
							return <AxiosResponse<ResponseResource<ActorNotificationDto>, any>> response
						},
					}
				},

				getPersonalNotificationsApi() {
					return {

						async getAllEntries(restRequestParams: RestRequestParams):Promise<AxiosResponse<SearchResult, any>> {
							const response = await apiResolver.resolveRequest("GET", apiResolver.resolveUrlForRestRequestParams("/api/notifications/personal", restRequestParams), undefined)
							return <AxiosResponse<SearchResult, any>> response
						},
						async getActorPersonalNotificationInfo():Promise<AxiosResponse<ActorPersonalNotificationInfoDto, any>> {
							const response = await apiResolver.resolveRequest("GET", "/api/notifications/personal/info", undefined)
							return <AxiosResponse<ActorPersonalNotificationInfoDto, any>> response
						},
						async getEntryById(id: String):Promise<AxiosResponse<ResponseResource<ActorNotificationDto>, any>> {
							const response = await apiResolver.resolveRequest("GET", `/api/notifications/personal/${id}`, undefined)
							return <AxiosResponse<ResponseResource<ActorNotificationDto>, any>> response
						},
						async changeReadStateOfPersonalNotification(id: String, read: Boolean):Promise<AxiosResponse<any, any>> {
							const response = await apiResolver.resolveRequest("PUT", apiResolver.resolveUrlForRestRequestParams(`/api/notifications/personal/${id}/readState`, RestRequestParams.create().addQueryParameter("read", read)), undefined)
							return <AxiosResponse<any, any>> response
						},
					}
				},

				getRegistryItemApi() {
					return {

						async getAllEntries(restRequestParams: RestRequestParams):Promise<AxiosResponse<SearchResult, any>> {
							const response = await apiResolver.resolveRequest("GET", apiResolver.resolveUrlForRestRequestParams("/api/registryItems", restRequestParams), undefined)
							return <AxiosResponse<SearchResult, any>> response
						},
						async getEntryById(id: String):Promise<AxiosResponse<ResponseResource<RegistryItemDto>, any>> {
							const response = await apiResolver.resolveRequest("GET", `/api/registryItems/${id}`, undefined)
							return <AxiosResponse<ResponseResource<RegistryItemDto>, any>> response
						},
						async updateEntry(id: String, body: RegistryItemDto):Promise<AxiosResponse<ResponseResource<RegistryItemDto>, any>> {
							const response = await apiResolver.resolveRequest("PUT", `/api/registryItems/${id}`, body)
							return <AxiosResponse<ResponseResource<RegistryItemDto>, any>> response
						},
					}
				},

				getReportUserErrorApi() {
					return {

						async reportUserBug(body: ReportUserBugRequest):Promise<AxiosResponse<any, any>> {
							const response = await apiResolver.resolveRequest("POST", "/api/error/reportedUserErrors/report", body)
							return <AxiosResponse<any, any>> response
						},
						async getAllEntries(restRequestParams: RestRequestParams):Promise<AxiosResponse<SearchResult, any>> {
							const response = await apiResolver.resolveRequest("GET", apiResolver.resolveUrlForRestRequestParams("/api/error/reportedUserErrors", restRequestParams), undefined)
							return <AxiosResponse<SearchResult, any>> response
						},
						async deleteEntry(id: String):Promise<AxiosResponse<any, any>> {
							const response = await apiResolver.resolveRequest("DELETE", `/api/error/reportedUserErrors/${id}`, undefined)
							return <AxiosResponse<any, any>> response
						},
						async getEntryById(id: String):Promise<AxiosResponse<ResponseResource<ReportedUserErrorDto>, any>> {
							const response = await apiResolver.resolveRequest("GET", `/api/error/reportedUserErrors/${id}`, undefined)
							return <AxiosResponse<ResponseResource<ReportedUserErrorDto>, any>> response
						},
						async updateEntry(id: String, body: ReportedUserErrorDto):Promise<AxiosResponse<ResponseResource<ReportedUserErrorDto>, any>> {
							const response = await apiResolver.resolveRequest("PUT", `/api/error/reportedUserErrors/${id}`, body)
							return <AxiosResponse<ResponseResource<ReportedUserErrorDto>, any>> response
						},
						async createNewEntry(body: ReportedUserErrorDto):Promise<AxiosResponse<ResponseResource<ReportedUserErrorDto>, any>> {
							const response = await apiResolver.resolveRequest("POST", "/api/error/reportedUserErrors", body)
							return <AxiosResponse<ResponseResource<ReportedUserErrorDto>, any>> response
						},
					}
				},

				getSecureKeyApi() {
					return {

						async getEntryById(id: String):Promise<AxiosResponse<ResponseResource<SecureKeyDto>, any>> {
							const response = await apiResolver.resolveRequest("GET", `/api/secureKeys/${id}`, undefined)
							return <AxiosResponse<ResponseResource<SecureKeyDto>, any>> response
						},
						async updateEntry(id: String, body: SecureKeyDto):Promise<AxiosResponse<ResponseResource<SecureKeyDto>, any>> {
							const response = await apiResolver.resolveRequest("PUT", `/api/secureKeys/${id}`, body)
							return <AxiosResponse<ResponseResource<SecureKeyDto>, any>> response
						},
						async getAllEntries(restRequestParams: RestRequestParams):Promise<AxiosResponse<SearchResult, any>> {
							const response = await apiResolver.resolveRequest("GET", apiResolver.resolveUrlForRestRequestParams("/api/secureKeys", restRequestParams), undefined)
							return <AxiosResponse<SearchResult, any>> response
						},
					}
				},

				getSelectableOptionContainerApi() {
					return {

						async getOptionsByContainer(containerName: String, isFilterOptions: Boolean):Promise<AxiosResponse<any[], any>> {
							const response = await apiResolver.resolveRequest("GET", apiResolver.resolveUrlForRestRequestParams(`/api/optionContainers/${containerName}/options`, RestRequestParams.create().addQueryParameter("isFilterOptions", isFilterOptions)), undefined)
							return <AxiosResponse<any[], any>> response
						},
						async getOptionContainers():Promise<AxiosResponse<String[], any>> {
							const response = await apiResolver.resolveRequest("GET", "/api/optionContainers", undefined)
							return <AxiosResponse<String[], any>> response
						},
					}
				},

				getServerInfoApi() {
					return {

						async getProjectVersion():Promise<AxiosResponse<VersionDetail, any>> {
							const response = await apiResolver.resolveRequest("GET", "/api/serverInfo/version/project", undefined)
							return <AxiosResponse<VersionDetail, any>> response
						},
						async baseApiInfo():Promise<AxiosResponse<ServerApiBaseInfo, any>> {
							const response = await apiResolver.resolveRequest("GET", "/api/serverInfo/baseApi", undefined)
							return <AxiosResponse<ServerApiBaseInfo, any>> response
						},
						async getLanguageById(id: String):Promise<AxiosResponse<LanguageInfo, any>> {
							const response = await apiResolver.resolveRequest("GET", `/api/serverInfo/languages/${id}`, undefined)
							return <AxiosResponse<LanguageInfo, any>> response
						},
						async getDefaultLanguage():Promise<AxiosResponse<LanguageInfo, any>> {
							const response = await apiResolver.resolveRequest("GET", "/api/serverInfo/languages/default", undefined)
							return <AxiosResponse<LanguageInfo, any>> response
						},
						async getCoreVersion():Promise<AxiosResponse<VersionDetail, any>> {
							const response = await apiResolver.resolveRequest("GET", "/api/serverInfo/version/core", undefined)
							return <AxiosResponse<VersionDetail, any>> response
						},
						async getLanguages():Promise<AxiosResponse<LanguageInfo[], any>> {
							const response = await apiResolver.resolveRequest("GET", "/api/serverInfo/languages", undefined)
							return <AxiosResponse<LanguageInfo[], any>> response
						},
					}
				},

				getServerTaskApi() {
					return {

						async updateEntry(id: String, body: ServerTaskDto):Promise<AxiosResponse<ResponseResource<ServerTaskDto>, any>> {
							const response = await apiResolver.resolveRequest("PUT", `/api/servertasks/${id}`, body)
							return <AxiosResponse<ResponseResource<ServerTaskDto>, any>> response
						},
						async getAllEntries(restRequestParams: RestRequestParams):Promise<AxiosResponse<SearchResult, any>> {
							const response = await apiResolver.resolveRequest("GET", apiResolver.resolveUrlForRestRequestParams("/api/servertasks", restRequestParams), undefined)
							return <AxiosResponse<SearchResult, any>> response
						},
						async executeByUUID(id: String):Promise<AxiosResponse<any, any>> {
							const response = await apiResolver.resolveRequest("POST", `/api/servertasks/${id}/execute`, undefined)
							return <AxiosResponse<any, any>> response
						},
						async getEntryById(id: String):Promise<AxiosResponse<ResponseResource<ServerTaskDto>, any>> {
							const response = await apiResolver.resolveRequest("GET", `/api/servertasks/${id}`, undefined)
							return <AxiosResponse<ResponseResource<ServerTaskDto>, any>> response
						},
					}
				},

				getUserApi() {
					return {

						async getAllEntries(restRequestParams: RestRequestParams):Promise<AxiosResponse<SearchResult, any>> {
							const response = await apiResolver.resolveRequest("GET", apiResolver.resolveUrlForRestRequestParams("/api/users", restRequestParams), undefined)
							return <AxiosResponse<SearchResult, any>> response
						},
						async deleteEntry(id: String):Promise<AxiosResponse<any, any>> {
							const response = await apiResolver.resolveRequest("DELETE", `/api/users/${id}`, undefined)
							return <AxiosResponse<any, any>> response
						},
						async getViewModelForResetPasswordByEmail(tokenValue: String):Promise<AxiosResponse<UserPersonalAccountResetPasswordByEmailVM, any>> {
							const response = await apiResolver.resolveRequest("GET", `/api/users/resetPasswordByEmailVM/${tokenValue}`, undefined)
							return <AxiosResponse<UserPersonalAccountResetPasswordByEmailVM, any>> response
						},
						async changeUserPassword(body: UserPersonalAccountChangePasswordVM):Promise<AxiosResponse<any, any>> {
							const response = await apiResolver.resolveRequest("POST", "/api/users/personalAccount/changePassword", body)
							return <AxiosResponse<any, any>> response
						},
						async getEntryById(id: String):Promise<AxiosResponse<ResponseResource<UserDto>, any>> {
							const response = await apiResolver.resolveRequest("GET", `/api/users/${id}`, undefined)
							return <AxiosResponse<ResponseResource<UserDto>, any>> response
						},
						async updateEntry(id: String, body: UserDto):Promise<AxiosResponse<ResponseResource<UserDto>, any>> {
							const response = await apiResolver.resolveRequest("PUT", `/api/users/${id}`, body)
							return <AxiosResponse<ResponseResource<UserDto>, any>> response
						},
						async resetPasswordByEmailVM(body: UserPersonalAccountResetPasswordByEmailVM):Promise<AxiosResponse<any, any>> {
							const response = await apiResolver.resolveRequest("POST", "/api/users/resetPasswordByEmailVM", body)
							return <AxiosResponse<any, any>> response
						},
						async savePersonalAccount(body: UserPersonalAccountVM):Promise<AxiosResponse<UserPersonalAccountVM, any>> {
							const response = await apiResolver.resolveRequest("POST", "/api/users/personalAccount", body)
							return <AxiosResponse<UserPersonalAccountVM, any>> response
						},
						async deleteAccount(body: UserPersonalAccountDeleteVM):Promise<AxiosResponse<any, any>> {
							const response = await apiResolver.resolveRequest("DELETE", "/api/users/personalAccount", body)
							return <AxiosResponse<any, any>> response
						},
						async getPersonalAccount():Promise<AxiosResponse<UserPersonalAccountVM, any>> {
							const response = await apiResolver.resolveRequest("GET", "/api/users/personalAccount", undefined)
							return <AxiosResponse<UserPersonalAccountVM, any>> response
						},
						async resetPassword(body: UserPersonalAccountForgotPasswordVM):Promise<AxiosResponse<any, any>> {
							const response = await apiResolver.resolveRequest("POST", "/api/users/resetPassword", body)
							return <AxiosResponse<any, any>> response
						},
					}
				},

				getUserRegistrationApi() {
					return {

						async getConfiguration():Promise<AxiosResponse<RegistrationConfiguration, any>> {
							const response = await apiResolver.resolveRequest("GET", "/api/userRegistrations/configuration", undefined)
							return <AxiosResponse<RegistrationConfiguration, any>> response
						},
						async getViewModelForRegistrationWithInvitation(tokenValue: String):Promise<AxiosResponse<UserRegistrationWithInvitationVM, any>> {
							const response = await apiResolver.resolveRequest("GET", `/api/userRegistrations/registrationByInvitationVM/${tokenValue}`, undefined)
							return <AxiosResponse<UserRegistrationWithInvitationVM, any>> response
						},
						async sendInvitation(body: SendUserRegistrationInvitationVM):Promise<AxiosResponse<any, any>> {
							const response = await apiResolver.resolveRequest("POST", "/api/userRegistrations/invitations", body)
							return <AxiosResponse<any, any>> response
						},
						async confirmEmail(body: ConfirmEmailByTokenBody):Promise<AxiosResponse<any, any>> {
							const response = await apiResolver.resolveRequest("POST", "/api/userRegistrations/confirmEmail", body)
							return <AxiosResponse<any, any>> response
						},
						async deleteEntry(id: String):Promise<AxiosResponse<any, any>> {
							const response = await apiResolver.resolveRequest("DELETE", `/api/userRegistrations/${id}`, undefined)
							return <AxiosResponse<any, any>> response
						},
						async confirmByAdmin(userRegistrationUUID: String):Promise<AxiosResponse<any, any>> {
							const response = await apiResolver.resolveRequest("POST", `/api/userRegistrations/confirmByAdmin/${userRegistrationUUID}`, undefined)
							return <AxiosResponse<any, any>> response
						},
						async getEntryById(id: String):Promise<AxiosResponse<ResponseResource<UserRegistrationDto>, any>> {
							const response = await apiResolver.resolveRequest("GET", `/api/userRegistrations/${id}`, undefined)
							return <AxiosResponse<ResponseResource<UserRegistrationDto>, any>> response
						},
						async updateEntry(id: String, body: UserRegistrationDto):Promise<AxiosResponse<ResponseResource<UserRegistrationDto>, any>> {
							const response = await apiResolver.resolveRequest("PUT", `/api/userRegistrations/${id}`, body)
							return <AxiosResponse<ResponseResource<UserRegistrationDto>, any>> response
						},
						async getAllEntries(restRequestParams: RestRequestParams):Promise<AxiosResponse<SearchResult, any>> {
							const response = await apiResolver.resolveRequest("GET", apiResolver.resolveUrlForRestRequestParams("/api/userRegistrations", restRequestParams), undefined)
							return <AxiosResponse<SearchResult, any>> response
						},
						async updateRegistrationByInvitation(body: UserRegistrationWithInvitationVM):Promise<AxiosResponse<any, any>> {
							const response = await apiResolver.resolveRequest("POST", "/api/userRegistrations/byInvitation", body)
							return <AxiosResponse<any, any>> response
						},
						async getRegistrationInvitationTemplate():Promise<AxiosResponse<SendUserRegistrationInvitationVM, any>> {
							const response = await apiResolver.resolveRequest("GET", "/api/userRegistrations/invitations/template", undefined)
							return <AxiosResponse<SendUserRegistrationInvitationVM, any>> response
						},
						async createNewRegistrationFromScratch(body: UserRegistrationVM):Promise<AxiosResponse<any, any>> {
							const response = await apiResolver.resolveRequest("POST", "/api/userRegistrations/fromScratch", body)
							return <AxiosResponse<any, any>> response
						},
					}
				},

				getUserRoleApi() {
					return {

						async deleteEntry(id: String):Promise<AxiosResponse<any, any>> {
							const response = await apiResolver.resolveRequest("DELETE", `/api/userRoles/${id}`, undefined)
							return <AxiosResponse<any, any>> response
						},
						async getEntryById(id: String):Promise<AxiosResponse<ResponseResource<UserRoleDto>, any>> {
							const response = await apiResolver.resolveRequest("GET", `/api/userRoles/${id}`, undefined)
							return <AxiosResponse<ResponseResource<UserRoleDto>, any>> response
						},
						async updateEntry(id: String, body: UserRoleDto):Promise<AxiosResponse<ResponseResource<UserRoleDto>, any>> {
							const response = await apiResolver.resolveRequest("PUT", `/api/userRoles/${id}`, body)
							return <AxiosResponse<ResponseResource<UserRoleDto>, any>> response
						},
						async getAllEntries(restRequestParams: RestRequestParams):Promise<AxiosResponse<SearchResult, any>> {
							const response = await apiResolver.resolveRequest("GET", apiResolver.resolveUrlForRestRequestParams("/api/userRoles", restRequestParams), undefined)
							return <AxiosResponse<SearchResult, any>> response
						},
						async createNewEntry(body: UserRoleDto):Promise<AxiosResponse<ResponseResource<UserRoleDto>, any>> {
							const response = await apiResolver.resolveRequest("POST", "/api/userRoles", body)
							return <AxiosResponse<ResponseResource<UserRoleDto>, any>> response
						},
					}
				},

				getWebContentAreaApi() {
					return {

						async getEntryById(id: String):Promise<AxiosResponse<ResponseResource<WebContentAreaDto>, any>> {
							const response = await apiResolver.resolveRequest("GET", `/api/webContentAreas/${id}`, undefined)
							return <AxiosResponse<ResponseResource<WebContentAreaDto>, any>> response
						},
						async updateEntry(id: String, body: WebContentAreaDto):Promise<AxiosResponse<ResponseResource<WebContentAreaDto>, any>> {
							const response = await apiResolver.resolveRequest("PUT", `/api/webContentAreas/${id}`, body)
							return <AxiosResponse<ResponseResource<WebContentAreaDto>, any>> response
						},
						async getAllEntries(restRequestParams: RestRequestParams):Promise<AxiosResponse<SearchResult, any>> {
							const response = await apiResolver.resolveRequest("GET", apiResolver.resolveUrlForRestRequestParams("/api/webContentAreas", restRequestParams), undefined)
							return <AxiosResponse<SearchResult, any>> response
						},
					}
				},

				getWebContentApi() {
					return {

						async createNewEntry(body: WebContentDto):Promise<AxiosResponse<ResponseResource<WebContentDto>, any>> {
							const response = await apiResolver.resolveRequest("POST", "/api/webContents", body)
							return <AxiosResponse<ResponseResource<WebContentDto>, any>> response
						},
						async loadUniqueWebContentByArea(area: String):Promise<AxiosResponse<WebContentDto, any>> {
							const response = await apiResolver.resolveRequest("GET", `/api/webContents/contentAreas/${area}`, undefined)
							return <AxiosResponse<WebContentDto, any>> response
						},
						async deleteEntry(id: String):Promise<AxiosResponse<any, any>> {
							const response = await apiResolver.resolveRequest("DELETE", `/api/webContents/${id}`, undefined)
							return <AxiosResponse<any, any>> response
						},
						async getEntryById(id: String):Promise<AxiosResponse<ResponseResource<WebContentDto>, any>> {
							const response = await apiResolver.resolveRequest("GET", `/api/webContents/${id}`, undefined)
							return <AxiosResponse<ResponseResource<WebContentDto>, any>> response
						},
						async updateEntry(id: String, body: WebContentDto):Promise<AxiosResponse<ResponseResource<WebContentDto>, any>> {
							const response = await apiResolver.resolveRequest("PUT", `/api/webContents/${id}`, body)
							return <AxiosResponse<ResponseResource<WebContentDto>, any>> response
						},
						async getAllEntries(restRequestParams: RestRequestParams):Promise<AxiosResponse<SearchResult, any>> {
							const response = await apiResolver.resolveRequest("GET", apiResolver.resolveUrlForRestRequestParams("/api/webContents", restRequestParams), undefined)
							return <AxiosResponse<SearchResult, any>> response
						},
					}
				},

			}
		}
	}
}

// ################ API Method declaration end ################"