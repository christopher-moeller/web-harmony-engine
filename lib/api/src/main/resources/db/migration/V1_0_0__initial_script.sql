create table if not exists app_application_exception
(
    uuid           uuid not null
        primary key,
    created_at     timestamp(6),
    created_by     varchar(255),
    updated_at     timestamp(6),
    updated_by     varchar(255),
    code_location  varchar(255)
        constraint app_application_exception_code_location_check
            check ((code_location)::text = ANY
                   ((ARRAY ['FRONTEND'::character varying, 'BACKEND'::character varying])::text[])),
    description    varchar(1000),
    exception_type varchar(255),
    log            text,
    message        varchar(255),
    stacktrace     text
);

create table if not exists app_email
(
    uuid         uuid not null
        primary key,
    created_at   timestamp(6),
    created_by   varchar(255),
    updated_at   timestamp(6),
    updated_by   varchar(255),
    event_log    text,
    from_email   varchar(255),
    html_message text,
    last_sending timestamp(6),
    state        varchar(255)
        constraint app_email_state_check
            check ((state)::text = ANY
                   ((ARRAY ['CREATED'::character varying, 'SENDING'::character varying, 'SEND'::character varying, 'ERROR_DURING_SENDING'::character varying])::text[])),
    subject      varchar(255),
    to_email     varchar(255)
);

create table if not exists app_file
(
    uuid         uuid  not null
        primary key,
    created_at   timestamp(6),
    created_by   varchar(255),
    updated_at   timestamp(6),
    updated_by   varchar(255),
    file_as_byte bytea not null,
    file_name    varchar(255),
    is_temp      boolean default false,
    size         bigint,
    type         varchar(255),
    web_type     varchar(255)
);

create table if not exists app_file_app_email
(
    email_id uuid not null
        constraint fk40fc5e8vnf3h0vmxymsrr4wju
            references app_email,
    file_id  uuid not null
        constraint fk4acvf6kollua5w2a01t3vry4
            references app_file,
    primary key (email_id, file_id)
);

create table if not exists app_harmony_event
(
    uuid       uuid not null
        primary key,
    created_at timestamp(6),
    created_by varchar(255),
    updated_at timestamp(6),
    updated_by varchar(255),
    java_type  varchar(255),
    payload    text
);

create table if not exists app_i18n_entity_attribute
(
    uuid          uuid         not null
        primary key,
    attribute     varchar(255) not null,
    entity_class  varchar(255) not null,
    is_core_entry boolean      not null
);

create table if not exists app_actor_notification_event_type
(
    uuid        uuid not null
        primary key,
    created_at  timestamp(6),
    created_by  varchar(255),
    updated_at  timestamp(6),
    updated_by  varchar(255),
    unique_name varchar(255),
    is_active   boolean default true,
    description uuid
        constraint fkm3khi67rrgawo1m0cdpgnix4j
            references app_i18n_entity_attribute,
    label       uuid
        constraint fkg4cx37d0qjsqh8uo6inowwi89
            references app_i18n_entity_attribute
);

create table if not exists app_actor_right
(
    uuid                                uuid not null
        primary key,
    created_at                          timestamp(6),
    created_by                          varchar(255),
    updated_at                          timestamp(6),
    updated_by                          varchar(255),
    unique_name                         varchar(255),
    is_allowed_for_system_actor         boolean default true,
    is_allowed_for_unknown_public_actor boolean default false,
    description                         uuid
        constraint fkstbb9nmm87cp5kl3eya7hc5aq
            references app_i18n_entity_attribute,
    label                               uuid
        constraint fkgfwrg12vkcciq4kdksthm75cy
            references app_i18n_entity_attribute
);

create table if not exists app_cron_job
(
    uuid             uuid not null
        primary key,
    created_at       timestamp(6),
    created_by       varchar(255),
    updated_at       timestamp(6),
    updated_by       varchar(255),
    cron_trigger     varchar(255),
    is_activated     boolean,
    java_class       varchar(255),
    last_executed_at timestamp(6),
    description      uuid
        constraint fkc42bjb5tvouy5stcl5vly6w2q
            references app_i18n_entity_attribute,
    label            uuid
        constraint fkdngv98vnebqc7bb4w1j3cclpc
            references app_i18n_entity_attribute
);

create table if not exists app_i18n_entity_attribute_value
(
    uuid             uuid         not null
        primary key,
    language         varchar(255) not null
        constraint app_i18n_entity_attribute_value_language_check
            check ((language)::text = ANY
                   ((ARRAY ['ENGLISH'::character varying, 'GERMAN'::character varying])::text[])),
    translation      varchar(255),
    entity_attribute uuid         not null
        constraint fkskccaj951k4y3socqw9vs0gkh
            references app_i18n_entity_attribute
);

create table if not exists app_i18n_key_entry
(
    uuid          uuid    not null
        primary key,
    class_id      varchar(255),
    code_lines    text,
    code_location varchar(255)
        constraint app_i18n_key_entry_code_location_check
            check ((code_location)::text = ANY
                   ((ARRAY ['BACKEND'::character varying, 'FRONTEND'::character varying])::text[])),
    description   varchar(255),
    is_core_entry boolean not null,
    key           varchar(255),
    placeholders  varchar(255),
    constraint classidandkey
        unique (class_id, key)
);

create table if not exists app_i18n_translation
(
    uuid           uuid not null
        primary key,
    language       varchar(255)
        constraint app_i18n_translation_language_check
            check ((language)::text = ANY
                   ((ARRAY ['ENGLISH'::character varying, 'GERMAN'::character varying])::text[])),
    translation    varchar(255),
    i18n_key_entry uuid
        constraint fkju7xb4ybg689835iulf4ipua1
            references app_i18n_key_entry,
    constraint entryandlanguage
        unique (i18n_key_entry, language)
);

create table if not exists app_registry_item
(
    uuid            uuid not null
        primary key,
    created_at      timestamp(6),
    created_by      varchar(255),
    updated_at      timestamp(6),
    updated_by      varchar(255),
    unique_name     varchar(255),
    java_type       varchar(255),
    value_as_string varchar(255),
    description     uuid
        constraint fk2036jlelgs2wqjwi9t7kd1l6s
            references app_i18n_entity_attribute,
    label           uuid
        constraint fk1j39261x9sr3kjf263ccvqxwn
            references app_i18n_entity_attribute
);

create table if not exists app_reported_user_error
(
    uuid        uuid not null
        primary key,
    created_at  timestamp(6),
    created_by  varchar(255),
    updated_at  timestamp(6),
    updated_by  varchar(255),
    description varchar(1000),
    page        varchar(255)
);

create table if not exists app_file_app_reported_user_error
(
    reported_user_error_id uuid not null
        constraint fk21jgsmyeqtnuenibstvdg6ljd
            references app_reported_user_error,
    file_id                uuid not null
        constraint fk5g25gt55imgb6xi9bvwfi7yvn
            references app_file,
    primary key (reported_user_error_id, file_id)
);

create table if not exists app_server_task
(
    uuid           uuid not null
        primary key,
    created_at     timestamp(6),
    created_by     varchar(255),
    updated_at     timestamp(6),
    updated_by     varchar(255),
    is_required    boolean,
    last_execution timestamp(6),
    task_id        varchar(255),
    task_name      varchar(255)
);

create table if not exists app_token
(
    uuid        uuid not null
        primary key,
    created_at  timestamp(6),
    created_by  varchar(255),
    updated_at  timestamp(6),
    updated_by  varchar(255),
    expires_at  timestamp(6),
    payload     text,
    token_value varchar(255),
    type        varchar(255)
        constraint app_token_type_check
            check ((type)::text = ANY
                   ((ARRAY ['REGISTRATION_INVITATION'::character varying, 'REGISTRATION_EMAIL_CONFIRMATION'::character varying, 'USER_ACCESS_TOKEN'::character varying, 'USER_RESET_PASSWORD_TOKEN'::character varying])::text[]))
);

create table if not exists app_user_registration
(
    uuid       uuid not null
        primary key,
    created_at timestamp(6),
    created_by varchar(255),
    updated_at timestamp(6),
    updated_by varchar(255),
    email      varchar(255),
    state      varchar(255)
        constraint app_user_registration_state_check
            check ((state)::text = ANY
                   ((ARRAY ['INVITED'::character varying, 'WAITING_FOR_EMAIL_CONFIRMATION'::character varying, 'WAITING_FOR_ADMIN_CONFIRMATION'::character varying, 'USER_CREATED'::character varying])::text[])),
    state_data xml,
    workflow   varchar(255)
        constraint app_user_registration_workflow_check
            check ((workflow)::text = ANY
                   ((ARRAY ['INVITATION_REGISTRATION_EMAIL'::character varying, 'REGISTRATION_EMAIL'::character varying, 'INVITATION_REGISTRATION_EMAIL_CONFIRMATION'::character varying, 'REGISTRATION_EMAIL_CONFIRMATION'::character varying, 'NO_REGISTRATION_ALLOWED'::character varying])::text[]))
);

create table if not exists app_user_role
(
    uuid        uuid not null
        primary key,
    created_at  timestamp(6),
    created_by  varchar(255),
    updated_at  timestamp(6),
    updated_by  varchar(255),
    unique_name varchar(255),
    description uuid
        constraint fkkek968e40bh1pe643jglai98a
            references app_i18n_entity_attribute,
    label       uuid
        constraint fke766oonofhutnrxhakddp5wiv
            references app_i18n_entity_attribute
);

create table if not exists app_user_access_config
(
    uuid       uuid not null
        primary key,
    created_at timestamp(6),
    created_by varchar(255),
    updated_at timestamp(6),
    updated_by varchar(255),
    is_admin   boolean,
    main_role  uuid
        constraint fkpsblj62k2nhfu6c2wkc5wq7pd
            references app_user_role
);

create table if not exists app_user
(
    uuid               uuid not null
        primary key,
    created_at         timestamp(6),
    created_by         varchar(255),
    updated_at         timestamp(6),
    updated_by         varchar(255),
    email              varchar(255),
    firstname          varchar(255),
    lastname           varchar(255),
    password           varchar(255),
    user_access_config uuid
        constraint uk_9pai6rk1r30d6s1auksu95pky
            unique
        constraint fk4c1p1grc09wxyya8jtoql79l6
            references app_user_access_config
);

create table if not exists app_actor
(
    actor_type  varchar(31) not null,
    uuid        uuid        not null
        primary key,
    unique_name varchar(255)
        constraint uk_alre0yq1g2a857i1jr14myf51
            unique,
    app_user    uuid
        constraint uk_jh8rwf5raqfpalmhhngwklnxm
            unique
        constraint fkw6q1tpag0rcpwikd53lbheg3
            references app_user
);

create table if not exists app_actor_notification
(
    uuid         uuid         not null
        primary key,
    created_at   timestamp(6),
    created_by   varchar(255),
    updated_at   timestamp(6),
    updated_by   varchar(255),
    caption      varchar(255) not null,
    payload      text,
    read         boolean default false,
    text_message text,
    event_type   uuid         not null
        constraint fkoscaenhqtaekkdlmpkoeupojd
            references app_actor_notification_event_type,
    recipient    uuid
        constraint fki9oxva757enj6x1r7uwa4jw61
            references app_actor
);

create table if not exists app_day_planning
(
    uuid       uuid not null
        primary key,
    created_at timestamp(6),
    created_by varchar(255),
    updated_at timestamp(6),
    updated_by varchar(255),
    date       date,
    notes      text,
    owner      uuid
        constraint fk8hbj65lsiv8nf7bx9ct8wvu18
            references app_actor
);

create table if not exists app_rights_roles
(
    role_id  uuid not null
        constraint fkh8gtse19dc3nfrx1cnw4kx76f
            references app_user_role,
    right_id uuid not null
        constraint fkemw0fmklbn25vyyhkp3tn4mr
            references app_actor_right,
    primary key (role_id, right_id)
);

create table if not exists app_rights_user_access
(
    user_access_id uuid not null
        constraint fkeph56kyomc85o4vcwcn2orb6o
            references app_user_access_config,
    right_id       uuid not null
        constraint fklewyoq1nb9102ni25kqhkaeoi
            references app_actor_right,
    primary key (user_access_id, right_id)
);

create table if not exists app_roles_user_access
(
    user_access_id uuid not null
        constraint fkjci8v6ajal58tkr3aeig710gm
            references app_user_access_config,
    role_id        uuid not null
        constraint fkryxa2vqpgbvdlbxlj3d7mby4r
            references app_user_role,
    primary key (user_access_id, role_id)
);

