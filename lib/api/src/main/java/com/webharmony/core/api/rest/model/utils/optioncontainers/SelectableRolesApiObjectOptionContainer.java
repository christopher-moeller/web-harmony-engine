package com.webharmony.core.api.rest.model.utils.optioncontainers;

import com.webharmony.core.api.rest.model.utils.apiobject.ApiObjectWithLabel;
import com.webharmony.core.api.rest.model.utils.optioncontainers.utils.AbstractUnPagedApiObjectObjectContainer;
import com.webharmony.core.context.ContextHolder;
import com.webharmony.core.data.enums.ECoreActorRight;
import com.webharmony.core.data.jpa.model.user.QAppUserRole;
import com.webharmony.core.configuration.security.ApplicationRight;

import java.util.List;

public class SelectableRolesApiObjectOptionContainer extends AbstractUnPagedApiObjectObjectContainer<ApiObjectWithLabel> {

    private static final QAppUserRole qAppUserRole = QAppUserRole.appUserRole;
    @Override
    public List<ApiObjectWithLabel> getSelectableOptions() {
        return createSelectableApiObjectsWithI18nLabel(qAppUserRole, r -> r.uuid, r -> r.label, ContextHolder.getContext().getContextLanguage());
    }

    @Override
    public ApplicationRight getApplicationRight() {
        return ECoreActorRight.CORE_LOAD_SELECTABLE_ROLES_FOR_OPTION_CONTAINER;
    }
}
