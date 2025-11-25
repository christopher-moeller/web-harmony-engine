package com.webharmony.core.api.rest.validation;

import com.webharmony.core.api.rest.jackson.CustomLocalDateTimeSerializer;
import com.webharmony.core.api.rest.model.view.user.UserLoginVM;
import com.webharmony.core.i18n.I18N;
import com.webharmony.core.i18n.I18nTranslation;
import com.webharmony.core.service.authentication.AuthenticationService;
import com.webharmony.core.service.authentication.UserLoginBlockService;
import com.webharmony.core.service.authentication.types.UserBlockedEntry;
import com.webharmony.core.service.data.validation.fieldvalidators.EmailFieldValidator;
import com.webharmony.core.service.data.validation.fieldvalidators.NotEmptyTextFieldValidator;
import com.webharmony.core.service.data.validation.utils.ValidationBuilder;
import com.webharmony.core.service.data.validation.utils.ValidationConfigBuilder;
import com.webharmony.core.service.data.validation.utils.ValidationContext;
import com.webharmony.core.utils.StringUtils;
import com.webharmony.core.utils.reflection.ReflectionUtils;
import org.springframework.stereotype.Component;

@Component
public class UserLoginVMValidation implements ValidationConfigBuilder<UserLoginVM>, I18nTranslation {

    private final I18N i18N = createI18nInstance(UserLoginVMValidation.class);

    private final UserLoginBlockService userLoginBlockService;

    public UserLoginVMValidation(UserLoginBlockService userLoginBlockService) {
        this.userLoginBlockService = userLoginBlockService;
    }

    @Override
    public void configureValidationBuilder(ValidationBuilder<UserLoginVM, UserLoginVM, ?, ? extends ValidationBuilder<UserLoginVM, ?, ?, ?>> builder) {
        builder.ofField(UserLoginVM::getUsername).withValidation(new NotEmptyTextFieldValidator<>(), new EmailFieldValidator<>());
        builder.ofField(UserLoginVM::getPassword).withValidation(new NotEmptyTextFieldValidator<>());
        builder.withValidation("INVALID_CREDENTIALS", this::hasValidCredentials);
    }

    private void hasValidCredentials(UserLoginVM userLoginVM, ValidationContext<UserLoginVM> userLoginVMValidationContext) {

        final String email = userLoginVM.getUsername();
        final String password = userLoginVM.getPassword();

        if(StringUtils.isNullOrEmpty(email) || StringUtils.isNullOrEmpty(password)) {
            return;
        }

        final String usernameFieldName = ReflectionUtils.getFieldNameByGetterMethod(UserLoginVM.class, UserLoginVM::getUsername);
        final String passwordFieldName = ReflectionUtils.getFieldNameByGetterMethod(UserLoginVM.class, UserLoginVM::getPassword);

        UserBlockedEntry userBlockedEntry = userLoginBlockService.getUserBlockedEntryByUser(email).orElse(null);
        if(userBlockedEntry != null && userBlockedEntry.isStillBlockedByTime()) {
            final String errorMessage = i18N.translate("Your account is blocked until {blockedUntil}").add("blockedUntil", CustomLocalDateTimeSerializer.parseDateToString(userBlockedEntry.getBlockedUntil())).build();
            userLoginVMValidationContext.addValidationErrorForSubPath(usernameFieldName, errorMessage);
            userLoginVMValidationContext.addValidationErrorForSubPath(passwordFieldName, errorMessage);
        } else {
            boolean passwordIsCorrect = getSpringBean(AuthenticationService.class).isUserPasswordValid(email, password);
            if(passwordIsCorrect) {
                userLoginBlockService.removeBlockedUser(email);
            } else {
                final String errorMessage = i18N.translate("The combination of email and password is not valid").build();
                userLoginVMValidationContext.addValidationErrorForSubPath(usernameFieldName, errorMessage);
                userLoginVMValidationContext.addValidationErrorForSubPath(passwordFieldName, errorMessage);
                userLoginBlockService.addLoginErrorAttempt(email);
            }
        }
    }
}
