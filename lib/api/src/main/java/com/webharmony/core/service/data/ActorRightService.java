package com.webharmony.core.service.data;

import com.querydsl.jpa.impl.JPAQuery;
import com.webharmony.core.api.rest.model.ActorRightDto;
import com.webharmony.core.configuration.security.ApplicationAccessRule;
import com.webharmony.core.configuration.security.ApplicationRight;
import com.webharmony.core.configuration.security.HandlerMethodAuthorizationResolver;
import com.webharmony.core.data.jpa.model.user.*;
import com.webharmony.core.data.jpa.model.utils.AbstractEnumEntity;
import com.webharmony.core.data.jpa.repository.AppActorRightRepository;
import com.webharmony.core.service.data.utils.AbstractEntityCrudService;
import com.webharmony.core.utils.assertions.Assert;
import com.webharmony.core.utils.exceptions.InternalServerException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.method.HandlerMethod;

import java.util.*;

@Service
@Slf4j
public class ActorRightService extends AbstractEntityCrudService<ActorRightDto, AppActorRight> {


    private static final QAppActorRight qAppActorRight = QAppActorRight.appActorRight;
    private static final QAppUser qAppUser = QAppUser.appUser;
    private static final QAppUserAccessConfig qAppUserAccessConfig = QAppUserAccessConfig.appUserAccessConfig;
    private static final QAppUserRole qAppUserRole = QAppUserRole.appUserRole;

    private final AppActorRightRepository appActorRightRepository;

    private Map<AppActorRight, ApplicationRight> entityApplicationRightMap;

    private final List<HandlerMethodAuthorizationResolver> handlerMethodAuthorizationResolvers;

    @PersistenceContext
    private EntityManager em;

    public ActorRightService(AppActorRightRepository appActorRightRepository, List<HandlerMethodAuthorizationResolver> handlerMethodAuthorizationResolvers) {
        this.appActorRightRepository = appActorRightRepository;
        this.handlerMethodAuthorizationResolvers = handlerMethodAuthorizationResolvers;
    }

    public Optional<ApplicationRight> getApplicationRightByEntity(AppActorRight entity) {
        ApplicationRight applicationRight = this.entityApplicationRightMap.get(entity);

        Assert.isNotNull(applicationRight)
                .withException(() -> new InternalServerException(String.format("No Right-Enum (ApplicationRight) for entity with name '%s' found", entity.getUniqueName())))
                .verifyWithSilentException();

        return Optional.ofNullable(applicationRight);
    }

    public List<AppActorRight> findByIsAllowedForUnknownPublicActor(Boolean isAllowedForUnknownPublicActor) {
        return appActorRightRepository.findByIsAllowedForUnknownPublicActor(isAllowedForUnknownPublicActor);
    }
    public List<AppActorRight> findByIsAllowedForSystemActor(Boolean isAllowedForSystemActor) {
        return appActorRightRepository.findByIsAllowedForSystemActor(isAllowedForSystemActor);
    }

    public AppActorRight getActorRightByUniqueName(String uniqueName) {

        JPAQuery<AppActorRight> query = new JPAQuery<>(getEntityManager())
                .select(qAppActorRight)
                .from(qAppActorRight)
                .where(qAppActorRight.uniqueName.eq(uniqueName));

        return query.fetchOne();
    }

    public ApplicationAccessRule getAccessRuleForHandlerMethod(HandlerMethod handlerMethod) {

        if(!handlerMethod.getBeanType().getPackage().getName().startsWith("com.webharmony")) {
            return ApplicationAccessRule.ofUnrestricted();
        }

        for (HandlerMethodAuthorizationResolver resolver : handlerMethodAuthorizationResolvers) {
            ApplicationAccessRule applicationAccessRule = resolver.buildAccessRule(handlerMethod);
            if(applicationAccessRule != null)
                return applicationAccessRule;
        }

        return null;
    }

    public void initRightCache(Map<Class<? extends AbstractEnumEntity>, List<Class<? extends Enum<?>>>> persistenceEnumEntityMap) {
        entityApplicationRightMap = new HashMap<>();
        List<AppActorRight> allEntities = appActorRightRepository.findAll();

        for (Class<? extends Enum<?>> enumClass : persistenceEnumEntityMap.get(AppActorRight.class)) {
            for (Enum<?> enumConstant : enumClass.getEnumConstants()) {
                ApplicationRight applicationRight = (ApplicationRight) enumConstant;
                AppActorRight entity = allEntities.stream()
                        .filter(e -> e.getUniqueName().equals(applicationRight.name()))
                        .findAny()
                        .orElseThrow(() -> new InternalServerException(String.format("No entity with name '%s' found", applicationRight.name())));

                entityApplicationRightMap.put(entity, applicationRight);
            }
        }

    }

    public Set<AppActorRight> loadAggregatedRightsOfRoles(AppUser user) {
        final JPAQuery<AppActorRight> query = new JPAQuery<>(em)
                .select(QAppActorRight.appActorRight)
                .distinct()
                .from(QAppUser.appUser)
                .join(QAppUser.appUser.userAccessConfig, QAppUserAccessConfig.appUserAccessConfig)
                .join(QAppUserAccessConfig.appUserAccessConfig.roles, QAppUserRole.appUserRole)
                .join(QAppUserRole.appUserRole.includedRights, QAppActorRight.appActorRight)
                .where(QAppUser.appUser.eq(user));

        return new HashSet<>(query.fetch());
    }

    public Set<AppActorRight> loadAdditionalRightsForUser(AppUser user) {
        final JPAQuery<AppActorRight> query = new JPAQuery<>(em)
                .select(QAppActorRight.appActorRight)
                .from(QAppUser.appUser)
                .join(QAppUser.appUser.userAccessConfig, QAppUserAccessConfig.appUserAccessConfig)
                .join(QAppUserAccessConfig.appUserAccessConfig.additionalRights, QAppActorRight.appActorRight)
                .where(QAppUser.appUser.eq(user));

        return new HashSet<>(query.fetch());
    }

    public boolean isInitialized() {
        return entityApplicationRightMap != null;
    }

    public Set<AppUser> findAllUsersWithRight(ApplicationRight applicationRight) {
        final AppActorRight neededActorRight = this.getActorRightByUniqueName(applicationRight.name());

        final JPAQuery<AppUser> query = new JPAQuery<>(em)
                .select(qAppUser)
                .from(qAppUser)
                .join(qAppUser.userAccessConfig, qAppUserAccessConfig)
                .join(qAppUserAccessConfig.roles, qAppUserRole)
                .where(qAppUserAccessConfig.isAdmin.isTrue().or(qAppUserAccessConfig.additionalRights.contains(neededActorRight).or(qAppUserRole.includedRights.contains(neededActorRight))));

        return new HashSet<>(query.fetch());
    }
}
