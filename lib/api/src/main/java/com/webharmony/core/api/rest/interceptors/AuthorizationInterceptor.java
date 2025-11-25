package com.webharmony.core.api.rest.interceptors;

import com.webharmony.core.configuration.security.ApplicationAccessRule;
import com.webharmony.core.context.ContextHolder;
import com.webharmony.core.data.jpa.model.actor.AbstractActor;
import com.webharmony.core.i18n.I18N;
import com.webharmony.core.i18n.I18nTranslation;
import com.webharmony.core.configuration.security.ApplicationRight;
import com.webharmony.core.service.data.ActorRightService;
import com.webharmony.core.utils.exceptions.ForbiddenException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;

import java.util.stream.Collectors;

@Component
public class AuthorizationInterceptor extends AbstractHandlerInterceptorAdapter implements I18nTranslation {

    private final I18N i18N = createI18nInstance(AuthorizationInterceptor.class);
    private final ActorRightService actorRightService;

    public AuthorizationInterceptor(ActorRightService actorRightService) {
        this.actorRightService = actorRightService;
    }

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) throws Exception {
        final AbstractActor currentActor = ContextHolder.getContext().getCurrentActor();
        if(handler instanceof HandlerMethod handlerMethod) {
            ApplicationAccessRule applicationAccessRule = actorRightService.getAccessRuleForHandlerMethod(handlerMethod);
            if(applicationAccessRule == null)
                return super.preHandle(request, response, handler);

            if(!applicationAccessRule.hasAccess(currentActor.getEffectiveRights())) {
                final String missingRights = applicationAccessRule.getApplicationRights()
                        .stream()
                        .map(ApplicationRight::getLabel)
                        .collect(Collectors.joining(", "));

                final String errorMessage;
                if(applicationAccessRule.getIsOrConnected()) {
                    errorMessage = i18N.translate("At least one of the following rights are required: {missingRights}").add("missingRights", missingRights).build();
                } else {
                    errorMessage = i18N.translate("Following rights are required: {missingRights}").add("missingRights", missingRights).build();
                }

                throw new ForbiddenException(errorMessage);
            }

        }

        return super.preHandle(request, response, handler);
    }
}
