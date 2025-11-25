package com.webharmony.core.utils.exceptions;

import com.webharmony.core.context.ContextHolder;
import com.webharmony.core.i18n.ECoreI18nStaticTranslations;
import com.webharmony.core.configuration.security.ApplicationRight;
import com.webharmony.core.service.data.ActorRightService;
import org.springframework.http.HttpStatus;

public class ForbiddenException extends ApplicationException {

    public ForbiddenException(ApplicationRight eCoreActorRight) {
        super(createMessageForRight(eCoreActorRight));
    }

    public ForbiddenException(String message) {
        super(message);
    }

    private static String createMessageForRight(ApplicationRight applicationRight) {
        String label = ContextHolder.getContext().getBean(ActorRightService.class)
                .getActorRightByUniqueName(applicationRight.name())
                .getLabel()
                .getValueByLanguage(ContextHolder.getContext().getContextLanguage())
                .orElseThrow();

        return ECoreI18nStaticTranslations.FORBIDDEN_EXCEPTION_TEXT.getI18n().add("permissionName", label).build();
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.FORBIDDEN;
    }
}
