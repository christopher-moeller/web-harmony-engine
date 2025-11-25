package com.webharmony.core.service.data.validation.fieldvalidators;

import com.querydsl.jpa.impl.JPAQuery;
import com.webharmony.core.api.rest.model.UserDto;
import com.webharmony.core.api.rest.model.UserRegistrationDto;
import com.webharmony.core.data.jpa.model.user.QAppUser;
import com.webharmony.core.data.jpa.model.userregistration.QAppUserRegistration;
import com.webharmony.core.i18n.I18N;
import com.webharmony.core.i18n.I18nTranslation;
import com.webharmony.core.service.data.validation.utils.NamedValidationInterface;
import com.webharmony.core.service.data.validation.utils.ValidationContext;
import com.webharmony.core.utils.StringUtils;

import java.util.UUID;

public class UserEmailNotExistsValidator<R> implements NamedValidationInterface<String, R>, I18nTranslation {

    private final I18N i18N = createI18nInstance(UserEmailNotExistsValidator.class);

    public static final String NAME = "USER_EMAIL_IS_UNIQUE_VALIDATION";

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

        if(!notExistsForUsers(email, validationContext) || !notExistsForRegistrations(email, validationContext)) {
            validationContext.addValidationError(i18N.translate("E-Mail already exists").build());
        }
    }

    private boolean notExistsForUsers(String email, ValidationContext<R> validationContext) {
        final JPAQuery<UUID> query = new JPAQuery<>(getEntityManager()).select(qAppUser.uuid)
                .from(qAppUser)
                .where(qAppUser.email.eq(email));

        if(validationContext.getRootSource() instanceof UserDto dto) {
            query.where(qAppUser.uuid.ne(UUID.fromString(dto.getId())));
        }
        return query.fetch().isEmpty();
    }

    private boolean notExistsForRegistrations(String email, ValidationContext<R> validationContext) {
        final JPAQuery<UUID> query = new JPAQuery<>(getEntityManager()).select(qAppUserRegistration.uuid)
                .from(qAppUserRegistration)
                .where(qAppUserRegistration.email.eq(email));

        if(validationContext.getRootSource() instanceof UserRegistrationDto dto) {
            query.where(qAppUserRegistration.uuid.ne(UUID.fromString(dto.getId())));
        }

        return query.fetch().isEmpty();
    }
}
