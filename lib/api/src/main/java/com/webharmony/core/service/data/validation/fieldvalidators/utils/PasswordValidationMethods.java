package com.webharmony.core.service.data.validation.fieldvalidators.utils;

import com.webharmony.core.i18n.I18N;
import com.webharmony.core.i18n.I18nTranslation;
import com.webharmony.core.service.data.validation.utils.ValidationContext;
import com.webharmony.core.utils.StringUtils;

public class PasswordValidationMethods implements I18nTranslation {

    private final I18N i18N = createI18nInstance(PasswordValidationMethods.class);

    private static PasswordValidationMethods instance = null;


    private PasswordValidationMethods() {

    }

    public static PasswordValidationMethods getInstance() {
        if(instance == null) {
            instance = new PasswordValidationMethods();
        }

        return instance;
    }

    public void validateNotEmpty(String password, ValidationContext<?> validationContext) {
        if(StringUtils.isNullOrEmpty(password)) {
            validationContext.addValidationError(getPasswordIsEmptyErrorText());
        }
    }

    public void validateComplex1(String password, ValidationContext<?> validationContext) {

        if(StringUtils.isNullOrEmpty(password)) {
            validationContext.addValidationError(getPasswordIsEmptyErrorText());
            return;
        }

        if (password.length() > 15 || password.length() < 8)
        {
            validationContext.addValidationError(i18N.translate("Password must be less than 20 and more than 8 characters in length.").build());
        }
        String upperCaseChars = "(.*[A-Z].*)";
        if (!password.matches(upperCaseChars ))
        {
            validationContext.addValidationError(i18N.translate("Password must have at least one uppercase character").build());
        }
        String lowerCaseChars = "(.*[a-z].*)";
        if (!password.matches(lowerCaseChars ))
        {
            validationContext.addValidationError(i18N.translate("Password must have at least one lowercase character").build());
        }
        String numbers = "(.*\\d.*)";
        if (!password.matches(numbers ))
        {
            validationContext.addValidationError(i18N.translate("Password must have at least one number").build());
        }
        String specialChars = "(.*[@,#$%].*$)";
        if (!password.matches(specialChars ))
        {
            validationContext.addValidationError(i18N.translate("Password must have at least one special character among @#$%").build());
        }
    }

    private String getPasswordIsEmptyErrorText() {
        return i18N.translate("Password is empty").build();
    }

}
