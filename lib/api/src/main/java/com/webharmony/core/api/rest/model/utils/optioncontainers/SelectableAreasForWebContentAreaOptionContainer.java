package com.webharmony.core.api.rest.model.utils.optioncontainers;

import com.webharmony.core.api.rest.model.utils.apiobject.ApiObjectWithLabel;
import com.webharmony.core.api.rest.model.utils.optioncontainers.utils.AbstractUnPagedApiObjectObjectContainer;
import com.webharmony.core.configuration.security.ApplicationRight;
import com.webharmony.core.data.enums.ECoreActorRight;
import com.webharmony.core.data.jpa.model.webcontent.QAppWebContentArea;

import java.util.List;

public class SelectableAreasForWebContentAreaOptionContainer extends AbstractUnPagedApiObjectObjectContainer<ApiObjectWithLabel> {

    private static final QAppWebContentArea qAppWebContentArea = QAppWebContentArea.appWebContentArea;

    @Override
    public List<ApiObjectWithLabel> getSelectableOptions() {
        return createSelectableApiObjectsWithI18nLabel(qAppWebContentArea, r -> r.uuid, r -> r.label);
    }

    @Override
    public ApplicationRight getApplicationRight() {
        return ECoreActorRight.CORE_WEB_CONTENT_CRUD;
    }
}
