package com.webharmony.core.testutils;

import com.webharmony.core.context.ContextHolder;
import com.webharmony.core.data.jpa.model.actor.AbstractActor;
import com.webharmony.core.data.jpa.model.user.AppUser;
import com.webharmony.core.service.ActorService;
import com.webharmony.core.service.authentication.types.AbstractAuthentication;

public class TestUserAuthentication extends AbstractAuthentication {


    public TestUserAuthentication(AppUser appUser) {
        super(getActorForAppUser(appUser));
    }

    private static AbstractActor getActorForAppUser(AppUser appUser) {
        return ContextHolder.getContext().getBean(ActorService.class)
                .getUserActorByAppUser(appUser);
    }
}
