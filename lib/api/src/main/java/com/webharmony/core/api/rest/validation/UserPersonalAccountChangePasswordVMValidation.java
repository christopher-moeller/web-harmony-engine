package com.webharmony.core.api.rest.validation;

import com.webharmony.core.api.rest.model.view.user.UserPersonalAccountChangePasswordVM;
import com.webharmony.core.i18n.I18N;
import com.webharmony.core.i18n.I18nTranslation;
import com.webharmony.core.service.data.validation.fieldvalidators.NotEmptyTextFieldValidator;
import com.webharmony.core.service.data.validation.fieldvalidators.PasswordFieldValidation;
import com.webharmony.core.service.data.validation.fieldvalidators.UserPasswordValidator;
import com.webharmony.core.service.data.validation.utils.ValidationBuilder;
import com.webharmony.core.service.data.validation.utils.ValidationConfigBuilder;
import com.webharmony.core.service.data.validation.utils.ValidationContext;
import com.webharmony.core.utils.reflection.ReflectionUtils;

import java.util.Objects;

public class UserPersonalAccountChangePasswordVMValidation implements ValidationConfigBuilder<UserPersonalAccountChangePasswordVM>, I18nTranslation {

    private final I18N i18N = createI18nInstance(UserPersonalAccountChangePasswordVMValidation.class);

    @Override
    public void configureValidationBuilder(ValidationBuilder<UserPersonalAccountChangePasswordVM, UserPersonalAccountChangePasswordVM, ?, ? extends ValidationBuilder<UserPersonalAccountChangePasswordVM, ?, ?, ?>> builder) {
        builder.ofField(UserPersonalAccountChangePasswordVM::getOldPassword).withValidation(new NotEmptyTextFieldValidator<>(), new UserPasswordValidator<>());
        builder.ofField(UserPersonalAccountChangePasswordVM::getNewPassword).withValidation(new NotEmptyTextFieldValidator<>(), new PasswordFieldValidation<>());
        builder.ofField(UserPersonalAccountChangePasswordVM::getNewPasswordAgain).withValidation(new NotEmptyTextFieldValidator<>());
        builder.withValidation("PASSWORDS_IDENTICAL_VALIDATION", this::checkIfPasswordsAreIdentical);
    }

    private void checkIfPasswordsAreIdentical(UserPersonalAccountChangePasswordVM userPersonalAccountChangePasswordVM, ValidationContext<?> context) {

        if(!Objects.equals(userPersonalAccountChangePasswordVM.getNewPassword(), userPersonalAccountChangePasswordVM.getNewPasswordAgain())) {
            final String passwordField = ReflectionUtils.getFieldNameByGetterMethod(UserPersonalAccountChangePasswordVM.class, UserPersonalAccountChangePasswordVM::getNewPassword);
            final String passwordAgainField = ReflectionUtils.getFieldNameByGetterMethod(UserPersonalAccountChangePasswordVM.class, UserPersonalAccountChangePasswordVM::getNewPasswordAgain);

            final String errorMessage = i18N.translate("Password fields are not identical").build();
            context.addValidationErrorForSubPath(passwordField, errorMessage);
            context.addValidationErrorForSubPath(passwordAgainField, errorMessage);
        }
    }
}
