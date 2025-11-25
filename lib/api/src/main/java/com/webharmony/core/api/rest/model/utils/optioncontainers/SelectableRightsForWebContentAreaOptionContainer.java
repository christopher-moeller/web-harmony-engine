package com.webharmony.core.api.rest.model.utils.optioncontainers;

import com.webharmony.core.api.rest.model.utils.apiobject.ApiObjectWithLabel;
import com.webharmony.core.api.rest.model.utils.optioncontainers.utils.AbstractUnPagedApiObjectObjectContainer;
import com.webharmony.core.configuration.security.ApplicationRight;
import com.webharmony.core.data.enums.ECoreActorRight;
import com.webharmony.core.data.jpa.model.user.QAppActorRight;

import java.util.List;

public class SelectableRightsForWebContentAreaOptionContainer extends AbstractUnPagedApiObjectObjectContainer<ApiObjectWithLabel> {

    private static final QAppActorRight qAppActorRight = QAppActorRight.appActorRight;

    @Override
    public List<ApiObjectWithLabel> getSelectableOptions() {
        return createSelectableApiObjectsWithI18nLabel(qAppActorRight, r -> r.uuid, r -> r.label);
    }

    @Override
    public ApplicationRight getApplicationRight() {
        return ECoreActorRight.CORE_WEB_CONTENT_AREA_CRUD;
    }
}
