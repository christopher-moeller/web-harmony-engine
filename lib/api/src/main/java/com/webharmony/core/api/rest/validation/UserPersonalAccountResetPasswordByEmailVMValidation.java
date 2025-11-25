package com.webharmony.core.api.rest.validation;

import com.webharmony.core.api.rest.model.view.user.UserPersonalAccountResetPasswordByEmailVM;
import com.webharmony.core.data.jpa.model.token.ETokenType;
import com.webharmony.core.i18n.I18N;
import com.webharmony.core.i18n.I18nTranslation;
import com.webharmony.core.service.TokenService;
import com.webharmony.core.service.data.validation.fieldvalidators.NotEmptyTextFieldValidator;
import com.webharmony.core.service.data.validation.fieldvalidators.PasswordFieldValidation;
import com.webharmony.core.service.data.validation.utils.ValidationBuilder;
import com.webharmony.core.service.data.validation.utils.ValidationConfigBuilder;
import com.webharmony.core.service.data.validation.utils.ValidationContext;
import com.webharmony.core.utils.reflection.ReflectionUtils;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class UserPersonalAccountResetPasswordByEmailVMValidation implements ValidationConfigBuilder<UserPersonalAccountResetPasswordByEmailVM>, I18nTranslation {

    private final I18N i18N = createI18nInstance(UserPersonalAccountResetPasswordByEmailVMValidation.class);

    private final TokenService tokenService;

    public UserPersonalAccountResetPasswordByEmailVMValidation(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @Override
    public void configureValidationBuilder(ValidationBuilder<UserPersonalAccountResetPasswordByEmailVM, UserPersonalAccountResetPasswordByEmailVM, ?, ? extends ValidationBuilder<UserPersonalAccountResetPasswordByEmailVM, ?, ?, ?>> builder) {
        builder.ofField(UserPersonalAccountResetPasswordByEmailVM::getTokenValue).withValidation(new NotEmptyTextFieldValidator<>());
        builder.ofField(UserPersonalAccountResetPasswordByEmailVM::getTokenValue).withValidation("TOKEN_NOT_VALID", this::checkIfTokenIsValid);
        builder.ofField(UserPersonalAccountResetPasswordByEmailVM::getPassword).withValidation(new PasswordFieldValidation<>());
        builder.ofField(UserPersonalAccountResetPasswordByEmailVM::getPasswordAgain).withValidation(new PasswordFieldValidation<>());
        builder.withValidation("PASSWORDS_IDENTICAL_VALIDATION", this::checkIfPasswordsAreIdentical);
    }

    private void checkIfPasswordsAreIdentical(UserPersonalAccountResetPasswordByEmailVM userPersonalAccountResetPasswordByEmailVM, ValidationContext<UserPersonalAccountResetPasswordByEmailVM> context) {
        if(!Objects.equals(userPersonalAccountResetPasswordByEmailVM.getPassword(), userPersonalAccountResetPasswordByEmailVM.getPasswordAgain())) {
            final String passwordField = ReflectionUtils.getFieldNameByGetterMethod(UserPersonalAccountResetPasswordByEmailVM.class, UserPersonalAccountResetPasswordByEmailVM::getPassword);
            final String passwordAgainField = ReflectionUtils.getFieldNameByGetterMethod(UserPersonalAccountResetPasswordByEmailVM.class, UserPersonalAccountResetPasswordByEmailVM::getPasswordAgain);

            final String errorMessage = i18N.translate("Password fields are not identical").build();
            context.addValidationErrorForSubPath(passwordField, errorMessage);
            context.addValidationErrorForSubPath(passwordAgainField, errorMessage);
        }
    }

    private void checkIfTokenIsValid(String tokenValue, ValidationContext<UserPersonalAccountResetPasswordByEmailVM> context) {
        if(!tokenService.isTokenValid(tokenValue, ETokenType.USER_RESET_PASSWORD_TOKEN)) {
            context.addValidationError(i18N.translate("Token is not valid").build());
        }
    }
}
