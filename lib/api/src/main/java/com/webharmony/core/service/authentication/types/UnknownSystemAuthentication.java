package com.webharmony.core.service.authentication.types;

import com.webharmony.core.data.jpa.model.actor.AppUnknownSystemActor;

public class UnknownSystemAuthentication extends AbstractAuthentication {

    public UnknownSystemAuthentication(AppUnknownSystemActor actor) {
        super(actor);
    }
}
