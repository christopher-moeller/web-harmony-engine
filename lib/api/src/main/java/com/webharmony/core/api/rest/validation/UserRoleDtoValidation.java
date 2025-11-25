package com.webharmony.core.api.rest.validation;

import com.webharmony.core.api.rest.model.UserRoleDto;
import com.webharmony.core.data.jpa.model.user.QAppUserRole;
import com.webharmony.core.service.data.validation.fieldvalidators.NotEmptyTextFieldValidator;
import com.webharmony.core.service.data.validation.fieldvalidators.SelectableOptionValidator;
import com.webharmony.core.service.data.validation.fieldvalidators.UniqueResourceI18nFieldValidator;
import com.webharmony.core.service.data.validation.utils.ValidationBuilder;
import com.webharmony.core.service.data.validation.utils.ValidationConfigBuilder;

public class UserRoleDtoValidation implements ValidationConfigBuilder<UserRoleDto> {

    private static final QAppUserRole qAppUserRole = QAppUserRole.appUserRole;

    @Override
    public void configureValidationBuilder(ValidationBuilder<UserRoleDto, UserRoleDto, ?, ? extends ValidationBuilder<UserRoleDto, ?, ?, ?>> builder) {
        builder.ofField(UserRoleDto::getLabel).withValidation(new NotEmptyTextFieldValidator<>())
                .ofField(UserRoleDto::getLabel).withValidation(new UniqueResourceI18nFieldValidator<>(qAppUserRole, q -> q.uuid, q -> q.label))
                .ofField(UserRoleDto::getDescription).withValidation(new NotEmptyTextFieldValidator<>())
                .ofField(UserRoleDto::getIncludedRights).withValidation(new SelectableOptionValidator<>(true));
    }
}
