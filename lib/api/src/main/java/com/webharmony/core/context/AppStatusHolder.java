package com.webharmony.core.context;

import com.webharmony.core.api.rest.model.view.ApplicationStatusVM;
import com.webharmony.core.configuration.EApplicationStatus;
import com.webharmony.core.data.enums.ECoreRegistry;
import com.webharmony.core.i18n.EI18nLanguage;
import com.webharmony.core.i18n.I18N;
import com.webharmony.core.i18n.I18nTranslation;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class AppStatusHolder implements I18nTranslation {

    final I18N i18N = createI18nInstance(AppStatusHolder.class);

    private static AppStatusHolder instance = null;

    private ApplicationStatusVM currentStatus = new ApplicationStatusVM();

    private AppStatusHolder() {

    }

    public static AppStatusHolder getInstance() {
        if(instance == null)
            instance = new AppStatusHolder();

        return instance;
    }

    public void setNewStatus(EApplicationStatus status, String userMessage) {
        setNewStatus(status, userMessage, userMessage);
    }

    public void setNewStatus(EApplicationStatus status, String userMessage, String technicalMessage) {
        ApplicationStatusVM newStatus = new ApplicationStatusVM();
        newStatus.setStatus(status);
        newStatus.setUserMessage(userMessage);
        newStatus.setTechnicalMessage(technicalMessage);
        setCurrentStatus(newStatus);
    }

    public boolean isStatusOk() {
        return getCurrentStatus().getStatus().equals(EApplicationStatus.OK);
    }

    public void resetStatus() {

        final EI18nLanguage systemLanguage = ContextHolder.getSpringContext() != null ? ECoreRegistry.I18N_DEFAULT_LANGUAGE.getTypedValue(EI18nLanguage.class) : I18N.CODING_LANGUAGE;

        if(!ContextHolder.isInitialized()) {
            setNewStatus(EApplicationStatus.STARTING, i18N.translate("Application is starting").build(systemLanguage));
            return;
        }

        if(getCurrentStatus().getStatus().equals(EApplicationStatus.MAINTENANCE_MODE))
            return;


        final AppStatusDetector.DetectionResult detectionResult = ContextHolder.getContext().getBean(AppStatusDetector.class).detect(systemLanguage);
        final List<String> userMessages = detectionResult.getUserMessages();
        final List<String> technicalMessages = detectionResult.getTechnicalMessages();


        if(!technicalMessages.isEmpty())
            userMessages.add(i18N.translate("Technical Problems: Please log in as administrator to grant access to the application.").build(systemLanguage));

        if(!userMessages.isEmpty()) {
            final String userMessage = String.join(System.lineSeparator(), userMessages);
            final String technicalMessage = String.join(System.lineSeparator(), technicalMessages);
            setNewStatus(EApplicationStatus.TECHNICAL_PROBLEMS, userMessage, technicalMessage);
            return;
        }

        setNewStatus(EApplicationStatus.OK, "");
    }
}
