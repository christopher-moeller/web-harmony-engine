package com.webharmony.core.api.rest.interceptors;

import com.webharmony.core.configuration.security.ApplicationAccessRule;
import com.webharmony.core.context.AppStatusHolder;
import com.webharmony.core.context.ContextHolder;
import com.webharmony.core.data.enums.ECoreActorRight;
import com.webharmony.core.data.enums.ECoreRegistry;
import com.webharmony.core.data.jpa.model.actor.AbstractActor;
import com.webharmony.core.i18n.I18N;
import com.webharmony.core.i18n.I18nTranslation;
import com.webharmony.core.service.data.ActorRightService;
import com.webharmony.core.utils.exceptions.ForbiddenException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;

@Component
public class StatusInterceptor extends AbstractHandlerInterceptorAdapter implements I18nTranslation  {

    private final I18N i18n = createI18nInstance(StatusInterceptor.class);
    private final ActorRightService actorRightService;

    public StatusInterceptor(ActorRightService actorRightService) {
        this.actorRightService = actorRightService;
    }

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) throws Exception {

        if(AppStatusHolder.getInstance().isStatusOk() && !ECoreRegistry.BLOCK_ALL_HTTP_REQUESTS.getBooleanValue())
            return super.preHandle(request, response, handler);

        if(handler instanceof HandlerMethod handlerMethod) {
            final ApplicationAccessRule applicationAccessRule = actorRightService.getAccessRuleForHandlerMethod(handlerMethod);
            if(applicationAccessRule == null || !applicationAccessRule.getIsRestricted())
                return super.preHandle(request, response, handler);

            final AbstractActor currentActor = ContextHolder.getContext().getCurrentActor();
            if(!currentActor.hasRight(ECoreActorRight.CORE_SERVER_STATUS_NOT_OK_USER_HAS_ACCESS)) {
                throw new ForbiddenException(i18n.translate("Technical Problems: Only users with special rights have access").build());
            }
        }

        return super.preHandle(request, response, handler);
    }
}
