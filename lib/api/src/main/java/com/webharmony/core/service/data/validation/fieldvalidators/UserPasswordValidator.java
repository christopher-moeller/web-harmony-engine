package com.webharmony.core.service.data.validation.fieldvalidators;

import com.webharmony.core.context.ContextHolder;
import com.webharmony.core.data.jpa.model.actor.AbstractActor;
import com.webharmony.core.data.jpa.model.actor.AppUserActor;
import com.webharmony.core.i18n.I18N;
import com.webharmony.core.i18n.I18nTranslation;
import com.webharmony.core.service.authentication.AuthenticationService;
import com.webharmony.core.service.data.validation.utils.NamedValidationInterface;
import com.webharmony.core.service.data.validation.utils.ValidationContext;
import com.webharmony.core.utils.exceptions.BadRequestException;

public class UserPasswordValidator<R> implements NamedValidationInterface<String, R>, I18nTranslation {

    public static final String NAME = "USER_PASSWORD_NOT_VALIDATOR";

    private final I18N i18N = createI18nInstance(UserPasswordValidator.class);

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public void validate(String password, ValidationContext<R> validationContext) {
        final AbstractActor currentActor = ContextHolder.getContext().getCurrentActor();
        if(currentActor instanceof AppUserActor appUserActor) {
            final String email = appUserActor.getUser().getEmail();
            boolean isPasswordValid = validationContext.getApplicationContext().getBean(AuthenticationService.class).isUserPasswordValid(email, password);
            if(!isPasswordValid) {
                validationContext.addValidationError(i18N.translate("Password is incorrect").build());
            }
        } else {
            throw new BadRequestException(i18N.translate("Current actor {actorId} is not a user").add("actorId", currentActor.getUniqueName()).build());
        }
    }
}
