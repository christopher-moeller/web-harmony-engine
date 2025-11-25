package com.webharmony.core.data.enums;

import com.webharmony.core.configuration.security.ApplicationRight;
import com.webharmony.core.data.enums.utils.PersistenceEnum;
import com.webharmony.core.data.jpa.model.webcontent.AppWebContentArea;
import com.webharmony.core.i18n.I18N;
import lombok.Getter;

public enum ECoreWebContentArea implements PersistenceEnum<AppWebContentArea>, ApplicationRight {

    CORE_IMPRINT_PAGE("Imprint page", "This content is responsible for the imprint page.", true),
    CORE_PRIVACY_NOTICE_PAGE("Privacy notice page", "This content is responsible for the privacy notice page.", true),
    CORE_ABOUT_US_PAGE("About us page", "This content is responsible for the about us page.", true);

    @Getter
    private final String label;
    private final String description;

    private final boolean uniqueContent;

    ECoreWebContentArea(String label, String description, boolean uniqueContent) {
        this.label = label;
        this.description = description;
        this.uniqueContent = uniqueContent;
    }


    @Override
    public void initEntity(AppWebContentArea entity) {
        entity.setLabel(I18N.entityAttribute(I18N.CODING_LANGUAGE, label));
        entity.setDescription(I18N.entityAttribute(I18N.CODING_LANGUAGE, description));
        entity.setOnlyOneContentInstanceAllowed(uniqueContent);
    }

    @Override
    public Class<AppWebContentArea> getEntityClass() {
        return AppWebContentArea.class;
    }

}
