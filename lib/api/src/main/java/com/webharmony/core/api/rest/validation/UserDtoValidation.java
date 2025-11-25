package com.webharmony.core.api.rest.validation;

import com.webharmony.core.api.rest.model.UserAccessDto;
import com.webharmony.core.api.rest.model.UserDto;
import com.webharmony.core.api.rest.model.utils.apiobject.ApiResource;
import com.webharmony.core.i18n.I18N;
import com.webharmony.core.i18n.I18nTranslation;
import com.webharmony.core.service.data.validation.fieldvalidators.EmailFieldValidator;
import com.webharmony.core.service.data.validation.fieldvalidators.NotEmptyTextFieldValidator;
import com.webharmony.core.service.data.validation.fieldvalidators.SelectableOptionValidator;
import com.webharmony.core.service.data.validation.utils.ValidationBuilder;
import com.webharmony.core.service.data.validation.utils.ValidationConfigBuilder;
import com.webharmony.core.service.data.validation.utils.ValidationContext;
import com.webharmony.core.utils.reflection.ReflectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class UserDtoValidation implements ValidationConfigBuilder<UserDto>, I18nTranslation {

    private final I18N i18N = createI18nInstance(UserDtoValidation.class);

    @Override
    public void configureValidationBuilder(ValidationBuilder<UserDto, UserDto, ?, ? extends ValidationBuilder<UserDto, ?, ?, ?>> builder) {
        builder.ofField(UserDto::getEmail).withValidation(new EmailFieldValidator<>())
                .ofField(UserDto::getFirstname).withValidation(new NotEmptyTextFieldValidator<>())
                .ofField(UserDto::getLastname).withValidation(new NotEmptyTextFieldValidator<>())
                .ofField(UserDto::getUserAccessConfig).withValidation("USER_GROUPS_INTEGRITY_VALIDATOR", this::validateUserGroupsIntegrity)
                .ofField(UserDto::getUserAccessConfig).ofField(UserAccessDto::getMainRole).withValidation(new SelectableOptionValidator<>(true))
                .ofField(UserAccessDto::getRoles).withValidation(new SelectableOptionValidator<>(true))
                .ofField(UserAccessDto::getAdditionalRights).withValidation(new SelectableOptionValidator<>(false));
    }

    private void validateUserGroupsIntegrity(UserAccessDto userAccessDto, ValidationContext<UserDto> validationContext) {
        final String mainRoleField = ReflectionUtils.getFieldNameByGetterMethod(UserAccessDto.class, UserAccessDto::getMainRole);
        final String allRolesField = ReflectionUtils.getFieldNameByGetterMethod(UserAccessDto.class, UserAccessDto::getRoles);

        Optional<ApiResource<?>> mainRole = Optional.ofNullable(userAccessDto.getMainRole());

        List<ApiResource<?>> allRoles = Optional.ofNullable(userAccessDto.getRoles())
                .orElseGet(Collections::emptyList);

        if(mainRole.isEmpty() && allRoles.isEmpty()) {
            return;
        }

        if(mainRole.isEmpty()) {
            validationContext.addValidationErrorForSubPath(mainRoleField, i18N.translate("Main Role must be set, when roles are set").build());
        } else {
            boolean mainRoleExistsInAllRoles = allRoles.stream().anyMatch(role -> Objects.equals(role.getPrimaryKey(), mainRole.get().getPrimaryKey()));
            if(!mainRoleExistsInAllRoles) {
                final String errorText = i18N.translate("Main Role not exists in all roles").build();
                validationContext.addValidationErrorForSubPath(mainRoleField, errorText);
                validationContext.addValidationErrorForSubPath(allRolesField, errorText);
            }
        }
    }
}
