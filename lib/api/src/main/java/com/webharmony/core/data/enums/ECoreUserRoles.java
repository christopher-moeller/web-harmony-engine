package com.webharmony.core.data.enums;

import com.webharmony.core.data.enums.utils.PersistenceEnum;
import com.webharmony.core.data.jpa.model.user.AppUserRole;
import com.webharmony.core.i18n.I18N;
import lombok.Getter;

public enum ECoreUserRoles implements PersistenceEnum<AppUserRole> {

    STANDARD_USER("Standard User", "Contains rights for the standard User. This role is automatically applied on new users after registration."),
    ADMIN_USER("Admin User", "Contains all rights for admin Users. If a user is marked as admin, he don't need this role, because the flag indicates that the user is a technical admin, which automatically has all rights of the application.'");

    @Getter
    private final String label;
    private final String description;

    ECoreUserRoles(String label, String description) {
        this.label = label;
        this.description = description;
    }


    @Override
    public void initEntity(AppUserRole entity) {
        entity.setLabel(I18N.entityAttribute(I18N.CODING_LANGUAGE, label));
        entity.setDescription(I18N.entityAttribute(I18N.CODING_LANGUAGE, description));
    }

    @Override
    public Class<AppUserRole> getEntityClass() {
        return AppUserRole.class;
    }

}
