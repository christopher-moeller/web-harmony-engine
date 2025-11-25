package com.webharmony.core.service;

import com.querydsl.jpa.impl.JPAQuery;
import com.webharmony.core.api.rest.model.ActorDto;
import com.webharmony.core.data.jpa.model.actor.*;
import com.webharmony.core.data.jpa.model.user.AppUser;
import com.webharmony.core.data.jpa.repository.ActorRepository;
import com.webharmony.core.utils.exceptions.EntityNotFoundException;
import com.webharmony.core.utils.exceptions.InternalServerException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class ActorService {

    private final Map<String, AbstractActor> actorCache = new HashMap<>();

    private final ActorRepository actorRepository;

    @PersistenceContext
    private EntityManager em;

    public ActorService(ActorRepository actorRepository) {
        this.actorRepository = actorRepository;
    }

    @Transactional
    public AppUserActor getUserActorByAppUser(AppUser appUser) {

        if(actorCache.containsKey(appUser.getEmail())) {
            final AppUserActor userActor = (AppUserActor) actorCache.get(appUser.getEmail());
            userActor.initRights();
            return userActor;
        }

        AppUserActor userActor = actorRepository.findByUniqueName(appUser.getEmail())
                .map(AppUserActor.class::cast)
                .orElseGet(() -> createNewUserActor(appUser));

        userActor.initRights();
        actorCache.put(userActor.getUniqueName(), userActor);

        return userActor;
    }

    @Transactional
    public void deleteAllReferencesForUser(AppUser user) {

        final List<AppUserActor> relevantUserActors = new JPAQuery<>(em)
                .select(QAppUserActor.appUserActor)
                .from(QAppUserActor.appUserActor)
                .where(QAppUserActor.appUserActor.uniqueName.eq(user.getEmail()).or(QAppUserActor.appUserActor.user.eq(user)))
                .fetch();

        for (AppUserActor relevantUserActor : relevantUserActors) {
            relevantUserActor.setUser(null);
            actorRepository.save(relevantUserActor);
        }
    }

    private AppUserActor createNewUserActor(AppUser appUser) {
        return actorRepository.save(new AppUserActor(appUser));
    }

    @Transactional
    public void connectUserWithActor(AppUser appUser) {
        AbstractActor abstractActor = this.actorRepository.findByUniqueName(appUser.getEmail())
                .orElseThrow(() -> new InternalServerException(String.format("No actor with unique name '%s' found", appUser.getEmail())));

        if(abstractActor instanceof AppUserActor appUserActor) {
            actorCache.remove(appUserActor.getUniqueName());
            appUserActor.setUser(appUser);
            final AppUserActor updatedActor = actorRepository.save(appUserActor);
            actorCache.put(updatedActor.getUniqueName(), updatedActor);

        } else {
            throw new InternalServerException(String.format("Actor with unique name '%s' is not an user actor", abstractActor.getUniqueName()));
        }
    }


    public AppInternalSystemActor getInternalSystemActor() {
        if(actorCache.containsKey(AppInternalSystemActor.ACTOR_UNIQUE_ID))
            return (AppInternalSystemActor) actorCache.get(AppInternalSystemActor.ACTOR_UNIQUE_ID);

        AppInternalSystemActor actor = actorRepository.findByUniqueName(AppInternalSystemActor.ACTOR_UNIQUE_ID)
                .map(AppInternalSystemActor.class::cast)
                .orElseGet(this::createNewInternalSystemActor);

        actor.initRights();
        actorCache.put(actor.getUniqueName(), actor);

        return actor;
    }

    public AppUnknownSystemActor getUnknownActor(String ipAddress) {

        if(actorCache.containsKey(ipAddress)) {
            return (AppUnknownSystemActor) actorCache.get(ipAddress);
        }

        final AppUnknownSystemActor actor = actorRepository.findByUniqueName(ipAddress)
                .map(AppUnknownSystemActor.class::cast)
                .orElseGet(() -> createNewUnknownSystemActor(ipAddress));

        actor.initRights();
        actorCache.put(actor.getUniqueName(), actor);

        return actor;
    }

    private AppUnknownSystemActor createNewUnknownSystemActor(String ipAddress) {
        return actorRepository.save(new AppUnknownSystemActor(ipAddress));
    }

    protected AppInternalSystemActor createNewInternalSystemActor() {
        return actorRepository.save(new AppInternalSystemActor());
    }

    public <T extends AbstractActor> T getActorByUUIDAndType(UUID uuid, Class<T> type) {
        return actorRepository.findById(uuid)
                .map(type::cast)
                .orElseThrow();
    }

    public AbstractActor getCurrentActor() {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication == null)
            return getInternalSystemActor();

        return authentication.getPrincipal() instanceof AbstractActor actor ? actor : null;
    }

    public void clearCache() {
        this.actorCache.clear();
    }

    public ActorDto getActorDtoByUUID(UUID uuid) {
        final AbstractActor actorEntity = actorRepository.findById(uuid)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Actor with UUID '%s not found'", uuid)));

        final ActorDto actorDto = new ActorDto();
        actorDto.setType(actorEntity.getClass().getSimpleName());
        actorDto.setUniqueName(actorEntity.getUniqueName());

        if(actorEntity instanceof AppUserActor userActor && userActor.getUser() != null) {
            actorDto.setUserId(Objects.toString(userActor.getUser().getUuid()));
        }

        return actorDto;
    }
}
