package com.webharmony.core.data.enums;

import com.webharmony.core.data.enums.utils.PersistenceEnum;
import com.webharmony.core.data.jpa.model.AppRegistryItem;
import com.webharmony.core.i18n.EI18nLanguage;
import com.webharmony.core.i18n.I18N;
import com.webharmony.core.service.data.validation.fieldvalidators.utils.EPasswordValidationType;
import com.webharmony.core.service.userregistration.EUserRegistrationWorkflow;
import com.webharmony.core.utils.assertions.Assert;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.function.Consumer;

@Slf4j
public enum ECoreRegistry implements PersistenceEnum<AppRegistryItem> {

    BLOCK_ALL_HTTP_REQUESTS("Block all http request", "If set to true, all requests are blocked except for users with special rights", Boolean.class, true),
    EMAIL_SEND_MAILS("Send E-Mails", "If set to false no email will be sent, but the entry in the database will be created.", Boolean.class, true),
    I18N_DEFAULT_LANGUAGE("Default language", "Default language for the application", EI18nLanguage.class, I18N.CODING_LANGUAGE),
    PASSWORD_VALIDATION_TYPE("Password validation type", "Type of password validation", EPasswordValidationType.class, EPasswordValidationType.NOT_EMPTY),
    USER_REGISTRATION_TOKEN_EXPIRES_IN_DAYS("User registration token expires in days", "Amount of days after the token is expired", Integer.class, 5),
    USER_REGISTRATION_WORKFLOW("User registration workflow", "The workflow if the current registration process.", EUserRegistrationWorkflow.class, EUserRegistrationWorkflow.NO_REGISTRATION_ALLOWED, entity -> log.info("changed registration workflow to {}", entity.getValue())),
    USER_RESET_PASSWORD_TOKEN_EXPIRES_IN_DAYS("User reset token expires in days", "Amount of days after the token is expired", Integer.class, 1),
    ;

    private final String label;
    private final String description;
    private final Class<?> javaType;
    private final Object defaultValue;

    @Getter
    private final Consumer<AppRegistryItem> onValueChangedConsumer;

    <T> ECoreRegistry(String label, String description, Class<T> javaType, T defaultValue) {
        this(label, description, javaType, defaultValue, null);
    }

    <T> ECoreRegistry(String label, String description, Class<T> javaType, T defaultValue, Consumer<AppRegistryItem> onValueChangedConsumer) {
        this.label = label;
        this.description = description;
        this.javaType = javaType;
        this.defaultValue = defaultValue;
        this.onValueChangedConsumer = onValueChangedConsumer;
    }

    @Override
    public void initEntity(AppRegistryItem entity) {
        entity.setLabel(I18N.entityAttribute(I18N.CODING_LANGUAGE, label));
        entity.setDescription(I18N.entityAttribute(I18N.CODING_LANGUAGE, description));
        entity.setValueAsString(String.valueOf(this.defaultValue));
        entity.setJavaType(this.javaType.getName());
        entity.setValueAsString(String.valueOf(this.defaultValue));
    }

    @Override
    public Class<AppRegistryItem> getEntityClass() {
        return AppRegistryItem.class;
    }

    @SuppressWarnings({"unchecked", "SameParameterValue"})
    public <T> T getTypedValue(Class<T> type) {
        Assert.isNotNull(type).verify();
        return (T) getValue();
    }

    public boolean getBooleanValue() {
        return getTypedValue(Boolean.class);
    }

    public Object getValue() {
        return getEntity().getValue();
    }
}
