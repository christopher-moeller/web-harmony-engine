package com.webharmony.core.service.authentication.types;

import com.webharmony.core.data.jpa.model.actor.AppUserActor;

public class JwtAuthentication extends AbstractAuthentication {

    public JwtAuthentication(AppUserActor actor) {
        super(actor);
    }
}
