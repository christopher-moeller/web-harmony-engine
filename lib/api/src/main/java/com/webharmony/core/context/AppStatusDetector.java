package com.webharmony.core.context;

import com.webharmony.core.api.rest.model.SecureKeyDto;
import com.webharmony.core.api.rest.model.i18n.I18nTranslationStatistic;
import com.webharmony.core.data.enums.ECoreRegistry;
import com.webharmony.core.i18n.EI18nLanguage;
import com.webharmony.core.i18n.I18N;
import com.webharmony.core.i18n.I18nTranslation;
import com.webharmony.core.service.SecureKeyService;
import com.webharmony.core.service.i18n.I18nEntityAttributeService;
import com.webharmony.core.service.i18n.I18nKeyEntryService;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class AppStatusDetector implements I18nTranslation {

    final I18N i18N = createI18nInstance(AppStatusDetector.class);

    public DetectionResult detect(final EI18nLanguage systemLanguage) {
        final List<String> userMessages = new ArrayList<>();
        final List<String> technicalMessages = new ArrayList<>();

        if(ECoreRegistry.BLOCK_ALL_HTTP_REQUESTS.getBooleanValue()) {
            technicalMessages.add(i18N.translate("Currently all non-public HTTP Requests are blocked for non admins. Please change this in the registry.").build(systemLanguage));
        }

        List<SecureKeyDto> emptySecureKeys = ContextHolder.getContext().getBean(SecureKeyService.class).getEmptyEntries();
        for (SecureKeyDto emptySecureKey : emptySecureKeys) {
            technicalMessages.add(i18N.translate("Secure key '{key}' is empty").add("key", emptySecureKey.getName()).build(systemLanguage));
        }

        final I18nTranslationStatistic keyEntryStatistics = ContextHolder.getContext().getBean(I18nKeyEntryService.class).getStatistics();
        if(keyEntryStatistics.getCountOfNotTranslatedItems() > 0) {
            technicalMessages.add(i18N.translate("There are {count} not translated i18n key entries").add("count", keyEntryStatistics.getCountOfNotTranslatedItems()).build(systemLanguage));
        }

        final I18nTranslationStatistic entityAttributeStatistics = ContextHolder.getContext().getBean(I18nEntityAttributeService.class).getStatistics();
        if(entityAttributeStatistics.getCountOfNotTranslatedItems() > 0) {
            technicalMessages.add(i18N.translate("There are {count} not translated i18n entity attributes").add("count", entityAttributeStatistics.getCountOfNotTranslatedItems()).build(systemLanguage));
        }

        return new DetectionResult(userMessages, technicalMessages);
    }

    @Data
    public static class DetectionResult {
        final List<String> userMessages;
        final List<String> technicalMessages;
    }


}
