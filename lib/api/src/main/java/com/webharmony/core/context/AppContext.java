package com.webharmony.core.context;

import com.webharmony.core.configuration.EProfile;
import com.webharmony.core.data.enums.ECoreRegistry;
import com.webharmony.core.data.jpa.model.actor.AbstractActor;
import com.webharmony.core.i18n.EI18nLanguage;
import com.webharmony.core.configuration.security.ApplicationRight;
import com.webharmony.core.service.ActorService;
import com.webharmony.core.service.authentication.types.AbstractAuthentication;
import com.webharmony.core.utils.exceptions.ForbiddenException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

@AllArgsConstructor
@Getter
public class AppContext {

    private final ApplicationContext springContext;
    private final EProfile profile;

    public <B> B getBean(Class<B> beanClass) {
        return springContext.getBean(beanClass);
    }

    public <B> Optional<B> getBeanIfExists(Class<B> beanClass) {
        try {
            return Optional.of(getBean(beanClass));
        } catch (BeansException e) {
            return Optional.empty();
        }
    }

    public AbstractActor getCurrentActor() {
        return getBean(ActorService.class).getCurrentActor();
    }

    public EI18nLanguage getContextLanguage() {
        return Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
                .map(AbstractAuthentication.class::cast)
                .map(AbstractAuthentication::getActorLanguage)
                .orElseGet(() -> ECoreRegistry.I18N_DEFAULT_LANGUAGE.getTypedValue(EI18nLanguage.class));

    }

    public void assertCurrentActorHasRight(ApplicationRight actorRight) {
        if (!currentActorAsRight(actorRight))
            throw new ForbiddenException(actorRight);
    }

    public boolean isProfileActive(EProfile profile) {
        return this.profile.equals(profile);
    }

    public boolean currentActorAsRight(ApplicationRight actorRight) {
        final AbstractActor currentActor = getCurrentActor();
        return currentActor != null && currentActor.hasRight(actorRight);
    }

}
