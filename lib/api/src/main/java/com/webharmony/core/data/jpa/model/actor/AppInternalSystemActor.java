package com.webharmony.core.data.jpa.model.actor;

import com.webharmony.core.context.ContextHolder;
import com.webharmony.core.configuration.security.ApplicationRight;
import com.webharmony.core.service.data.ActorRightService;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

import java.util.Set;
import java.util.stream.Collectors;

@Entity
@DiscriminatorValue(AppInternalSystemActor.ACTOR_TYPE)
public class AppInternalSystemActor extends AbstractActor {

    public static final String ACTOR_TYPE = "internal_system_actor";

    public static final String ACTOR_UNIQUE_ID = "internal_system";

    public AppInternalSystemActor() {
        setUniqueName(ACTOR_UNIQUE_ID);
    }

    @Override
    protected Set<ApplicationRight> buildEffectiveRights() {
        final ActorRightService actorRightService = ContextHolder.getContext().getBean(ActorRightService.class);
        return actorRightService.findByIsAllowedForSystemActor(true)
                .stream()
                .flatMap(a -> actorRightService.getApplicationRightByEntity(a).stream())
                .collect(Collectors.toSet());
    }
}
