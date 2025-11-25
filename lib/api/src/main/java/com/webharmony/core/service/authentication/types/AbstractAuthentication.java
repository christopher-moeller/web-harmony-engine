package com.webharmony.core.service.authentication.types;

import com.webharmony.core.data.jpa.model.actor.AbstractActor;
import com.webharmony.core.i18n.EI18nLanguage;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Collections;

public abstract class AbstractAuthentication implements Authentication {

    private final transient AbstractActor actor;
    protected AbstractAuthentication(AbstractActor actor) {
        this.actor = actor;
    }

    @Getter
    @Setter
    private EI18nLanguage actorLanguage;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList();
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getDetails() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return actor;
    }

    @Override
    public boolean isAuthenticated() {
        return true;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {

    }

    @Override
    public String getName() {
        return actor.getUniqueName();
    }
}
