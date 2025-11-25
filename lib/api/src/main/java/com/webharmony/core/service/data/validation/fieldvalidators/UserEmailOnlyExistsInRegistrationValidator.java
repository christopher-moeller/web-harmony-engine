package com.webharmony.core.service.data.validation.fieldvalidators;

import com.querydsl.jpa.impl.JPAQuery;
import com.webharmony.core.context.ContextHolder;
import com.webharmony.core.data.jpa.model.user.QAppUser;
import com.webharmony.core.data.jpa.model.userregistration.QAppUserRegistration;
import com.webharmony.core.i18n.I18N;
import com.webharmony.core.i18n.I18nTranslation;
import com.webharmony.core.service.EntityService;
import com.webharmony.core.service.data.validation.utils.NamedValidationInterface;
import com.webharmony.core.service.data.validation.utils.ValidationContext;
import com.webharmony.core.utils.StringUtils;
import jakarta.persistence.EntityManager;

import java.util.UUID;

public class UserEmailOnlyExistsInRegistrationValidator<R> implements NamedValidationInterface<String, R>, I18nTranslation {

    private final I18N i18N = createI18nInstance(UserEmailOnlyExistsInRegistrationValidator.class);

    public static final String NAME = "USER_EMAIL_IN_REGISTRATION_VALIDATION";

    private static final QAppUser qAppUser = QAppUser.appUser;
    private static final QAppUserRegistration qAppUserRegistration = QAppUserRegistration.appUserRegistration;

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public void validate(String email, ValidationContext<R> validationContext) {

        if(StringUtils.isNullOrEmpty(email))
            return;

        if(!notExistsForUsers(email)) {
            validationContext.addValidationError(i18N.translate("E-Mail already exists").build());
        }

        if(!existsForRegistrations(email)) {
            validationContext.addValidationError(i18N.translate("E-Mail is not allowed for registration").build());
        }
    }

    private boolean notExistsForUsers(String email) {
        final EntityManager em = ContextHolder.getContext().getBean(EntityService.class).getEntityManager();
        return new JPAQuery<>(em).select(qAppUser.uuid)
                .from(qAppUser)
                .where(qAppUser.email.eq(email))
                .fetch().isEmpty();
    }

    private boolean existsForRegistrations(String email) {
        final EntityManager em = ContextHolder.getContext().getBean(EntityService.class).getEntityManager();
        final JPAQuery<UUID> query = new JPAQuery<>(em).select(qAppUserRegistration.uuid)
                .from(qAppUserRegistration)
                .where(qAppUserRegistration.email.eq(email));

        return !query.fetch().isEmpty();
    }
}
