package com.webharmony.core.i18n;

import com.webharmony.core.api.rest.model.RestResourceInfo;
import com.webharmony.core.data.enums.utils.SecureKeyEnum;
import com.webharmony.core.service.data.validation.ValidatorValidationException;
import com.webharmony.core.service.data.validation.utils.ValidationIsMarkedAsAlwaysInvalidException;
import com.webharmony.core.utils.exceptions.ForbiddenException;
import com.webharmony.core.utils.exceptions.ResourceNotFoundException;
import lombok.Getter;

@Getter
public enum ECoreI18nStaticTranslations implements I18nStaticTranslations{

    FORBIDDEN_EXCEPTION_TEXT(ForbiddenException.class, "You don't have the permission '{permissionName}'"),
    LANGUAGE_GERMAN(EI18nLanguage.class, "German"),
    LANGUAGE_ENGLISH(EI18nLanguage.class, "English"),
    RESOURCE_NOT_FOUND_EXCEPTION_TEXT(ResourceNotFoundException.class, "Resource '{resourceName}' with id '{id}' not found"),
    REST_RESOURCE_INFO_NOT_FOUND(RestResourceInfo.class, "Resource with name '{resourceName}' not found"),
    SECURE_KEY_CANNOT_BE_EMPTY(SecureKeyEnum.class, "Key for name '{keyName}' cannot be empty"),
    VALIDATION_MARKED_AS_ALWAYS_INVALID(ValidationIsMarkedAsAlwaysInvalidException.class, "Validation is marked as always invalid"),
    VALIDATOR_VALIDATION_ERROR(ValidatorValidationException.class, "Validation Error: {countOfFailedValidations} validations of object of type '{validationType}' failed!");

    private final Class<?> callingClass;
    private final String defaultValue;

    ECoreI18nStaticTranslations(Class<?> callingClass, String defaultValue) {
        this.callingClass = callingClass;
        this.defaultValue = defaultValue;
    }

}
