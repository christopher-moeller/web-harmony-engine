package com.webharmony.core.data.jpa.model.actor;

import com.webharmony.core.context.ContextHolder;
import com.webharmony.core.data.jpa.model.user.AppActorRight;
import com.webharmony.core.data.jpa.model.user.AppUser;
import com.webharmony.core.data.jpa.model.user.AppUserAccessConfig;
import com.webharmony.core.configuration.security.ApplicationRight;
import com.webharmony.core.service.data.ActorRightService;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@DiscriminatorValue(AppUserActor.ACTOR_TYPE)
@NoArgsConstructor
@Getter
@Setter
public class AppUserActor extends AbstractActor {

    public static final String ACTOR_TYPE = "user_actor";

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "app_user", referencedColumnName = "uuid")
    private AppUser user;

    public AppUserActor(AppUser user) {
        setUser(user);
        setUniqueName(user.getIDRepresentation());
    }

    @Override
    protected Set<ApplicationRight> buildEffectiveRights() {

        if(user == null)
            return Collections.emptySet();


        final AppUserAccessConfig userAccessConfig = user.getUserAccessConfig();
        if(userAccessConfig == null)
            return Collections.emptySet();

        boolean isAdmin = Optional.ofNullable(userAccessConfig.getIsAdmin())
                .orElse(false);

        final ActorRightService actorRightService = ContextHolder.getContext().getBean(ActorRightService.class);

        final Set<AppActorRight> allActorRights;
        if(isAdmin) {
            allActorRights = new HashSet<>(actorRightService.getAllEntities());
        } else {
            allActorRights = new HashSet<>();
            allActorRights.addAll(actorRightService.loadAggregatedRightsOfRoles(user));

            allActorRights.addAll(actorRightService.loadAdditionalRightsForUser(user));
        }

        return allActorRights.stream()
                .flatMap(a -> actorRightService.getApplicationRightByEntity(a).stream())
                .collect(Collectors.toSet());
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
