package com.webharmony.core.service.data.validation.fieldvalidators;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.webharmony.core.i18n.I18N;
import com.webharmony.core.i18n.I18nTranslation;
import com.webharmony.core.service.data.validation.utils.NamedValidationInterface;
import com.webharmony.core.service.data.validation.utils.ValidationContext;
import com.webharmony.core.utils.JacksonUtils;

public class XmlStringFieldValidator<R> implements NamedValidationInterface<String, R>, I18nTranslation {

    private final I18N i18N = createI18nInstance(XmlStringFieldValidator.class);

    public static final String NAME = "XML_STRING_VALIDATION";

    private final Class<?> targetType;

    public XmlStringFieldValidator(Class<?> targetType) {
        this.targetType = targetType;
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public void validate(String xml, ValidationContext<R> validationContext) {
        if(!isValid(xml))
            validationContext.addValidationError(i18N.translate("XML is not valid").build());
    }

    private boolean isValid(String xmlAsString) {
        try {
            JacksonUtils.createDefaultXmlMapper().readValue(xmlAsString, targetType);
            return true;
        } catch (JsonProcessingException e) {
            return false;
        }
    }
}
