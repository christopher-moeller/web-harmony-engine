package com.webharmony.core.data.jpa.model.actor;

import com.webharmony.core.context.ContextHolder;
import com.webharmony.core.configuration.security.ApplicationRight;
import com.webharmony.core.service.data.ActorRightService;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;
import java.util.stream.Collectors;

@Entity
@DiscriminatorValue(AppUnknownSystemActor.ACTOR_TYPE)
@NoArgsConstructor
@Getter
@Setter
public class AppUnknownSystemActor extends AbstractActor {

    public static final String ACTOR_TYPE = "unknown_system_actor";

    public AppUnknownSystemActor(String ipAddress) {
        setUniqueName(ipAddress);
    }

    @Override
    protected Set<ApplicationRight> buildEffectiveRights() {
        final ActorRightService actorRightService = ContextHolder.getContext().getBean(ActorRightService.class);
        return actorRightService.findByIsAllowedForUnknownPublicActor(true)
                .stream()
                .flatMap(a -> actorRightService.getApplicationRightByEntity(a).stream())
                .collect(Collectors.toSet());
    }
}
