package com.webharmony.core.api.rest.validation;

import com.webharmony.core.api.rest.model.view.user.UserRegistrationVM;
import com.webharmony.core.i18n.I18N;
import com.webharmony.core.i18n.I18nTranslation;
import com.webharmony.core.service.data.validation.fieldvalidators.*;
import com.webharmony.core.service.data.validation.utils.NamedValidationInterface;
import com.webharmony.core.service.data.validation.utils.ValidationBuilder;
import com.webharmony.core.service.data.validation.utils.ValidationConfigBuilder;
import com.webharmony.core.service.data.validation.utils.ValidationContext;
import com.webharmony.core.utils.reflection.ReflectionUtils;

import java.util.Objects;

public class UserRegistrationVMValidation implements ValidationConfigBuilder<UserRegistrationVM>, I18nTranslation {

    private final I18N i18N = createI18nInstance(UserRegistrationVMValidation.class);

    private final boolean userRegistrationAlreadyExists;

    @SuppressWarnings("unused") // used by reflections
    public UserRegistrationVMValidation() {
        this(false);
    }

    public UserRegistrationVMValidation(boolean userRegistrationAlreadyExists) {
        this.userRegistrationAlreadyExists = userRegistrationAlreadyExists;
    }


    @Override
    public void configureValidationBuilder(ValidationBuilder<UserRegistrationVM, UserRegistrationVM, ?, ? extends ValidationBuilder<UserRegistrationVM, ?, ?, ?>> builder) {
        builder.ofField(UserRegistrationVM::getEmail).withValidation(new NotEmptyTextFieldValidator<>(), new LimitTextLengthValidator<>(), new EmailFieldValidator<>(), getEmailExistsValidation())
                .ofField(UserRegistrationVM::getFirstname).withValidation(new NotEmptyTextFieldValidator<>(), new LimitTextLengthValidator<>())
                .ofField(UserRegistrationVM::getLastname).withValidation(new NotEmptyTextFieldValidator<>(), new LimitTextLengthValidator<>())
                .ofField(UserRegistrationVM::getPassword).withValidation(new PasswordFieldValidation<>())
                .ofField(UserRegistrationVM::getPasswordAgain).withValidation(new PasswordFieldValidation<>())
                .withValidation("PASSWORDS_IDENTICAL_VALIDATION", this::checkIfPasswordsAreIdentical);
    }

    private NamedValidationInterface<String, UserRegistrationVM> getEmailExistsValidation() {
        return this.userRegistrationAlreadyExists ? new UserEmailOnlyExistsInRegistrationValidator<>() : new UserEmailNotExistsValidator<>();
    }

    private void checkIfPasswordsAreIdentical(UserRegistrationVM userRegistrationVM, ValidationContext<?> userRegistrationVMValidationContext) {

        if(!Objects.equals(userRegistrationVM.getPassword(), userRegistrationVM.getPasswordAgain())) {
            final String passwordField = ReflectionUtils.getFieldNameByGetterMethod(UserRegistrationVM.class, UserRegistrationVM::getPassword);
            final String passwordAgainField = ReflectionUtils.getFieldNameByGetterMethod(UserRegistrationVM.class, UserRegistrationVM::getPasswordAgain);

            final String errorMessage = i18N.translate("Password fields are not identical").build();
            userRegistrationVMValidationContext.addValidationErrorForSubPath(passwordField, errorMessage);
            userRegistrationVMValidationContext.addValidationErrorForSubPath(passwordAgainField, errorMessage);
        }
    }
}
