package com.webharmony.core.service.data;

import com.querydsl.jpa.impl.JPAQuery;
import com.webharmony.core.api.rest.controller.utils.dispacher.ControllerDispatcher;
import com.webharmony.core.api.rest.controller.utils.request.RequestContext;
import com.webharmony.core.api.rest.controller.utils.request.SearchRequestContext;
import com.webharmony.core.api.rest.model.ActorNotificationDto;
import com.webharmony.core.api.rest.model.ActorPersonalNotificationInfoDto;
import com.webharmony.core.context.ContextHolder;
import com.webharmony.core.data.jpa.model.actor.AbstractActor;
import com.webharmony.core.data.jpa.model.notifications.AppActorNotification;
import com.webharmony.core.data.jpa.model.notifications.QAppActorNotification;
import com.webharmony.core.i18n.I18N;
import com.webharmony.core.i18n.I18nTranslation;
import com.webharmony.core.service.searchcontainer.ActorPersonalNotificationSearchContainer;
import com.webharmony.core.service.searchcontainer.utils.SearchResult;
import com.webharmony.core.utils.exceptions.EntityNotFoundException;
import com.webharmony.core.utils.exceptions.InternalServerException;
import com.webharmony.core.utils.exceptions.MethodNotAllowedException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class ActorPersonalNotificationService implements ControllerDispatcher<ActorNotificationDto>, I18nTranslation {

    private final I18N i18N = createI18nInstance(ActorPersonalNotificationService.class);

    private final ActorNotificationCrudService actorNotificationCrudService;
    private final ActorPersonalNotificationSearchContainer searchContainer;

    @PersistenceContext
    private EntityManager em;

    public ActorPersonalNotificationService(ActorNotificationCrudService actorNotificationCrudService, ActorPersonalNotificationSearchContainer searchContainer) {
        this.actorNotificationCrudService = actorNotificationCrudService;
        this.searchContainer = searchContainer;
    }

    @Override
    public SearchResult getAllEntries(RequestContext requestContext) {
        if(requestContext instanceof SearchRequestContext searchRequestContext) {
            searchRequestContext.setDtoClass(ActorNotificationDto.class);
            return searchContainer.getSearchResultsByRequestParams(searchRequestContext);
        } else {
            throw new InternalServerException("Not implemented yet");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ActorNotificationDto getEntryById(UUID uuid, RequestContext requestContext) {
        ensureRecipientIsCurrentActor(uuid);
        return actorNotificationCrudService.getEntryById(uuid, requestContext);
    }

    @Override
    public ActorNotificationDto createNewEntry(ActorNotificationDto dto, RequestContext requestContext) {
        throw new MethodNotAllowedException();
    }

    @Override
    public ActorNotificationDto updateEntry(UUID uuid, ActorNotificationDto dto, RequestContext requestContext) {
        throw new MethodNotAllowedException();
    }

    @Override
    public void deleteEntry(UUID uuid, RequestContext requestContext) {
        throw new MethodNotAllowedException();
    }

    private void ensureRecipientIsCurrentActor(UUID uuid) {
        final AppActorNotification entity = this.actorNotificationCrudService.getEntityById(uuid);
        if(!ContextHolder.getContext().getCurrentActor().equals(entity.getRecipient())) {
            throw new EntityNotFoundException(i18N.translate("Personal Notification with id {id} not found").add("id", uuid).build());
        }
    }

    @Transactional(readOnly = true)
    public ActorPersonalNotificationInfoDto getActorPersonalNotificationInfo() {

        final AbstractActor currentActor = ContextHolder.getContext().getCurrentActor();


        final ActorPersonalNotificationInfoDto info = new ActorPersonalNotificationInfoDto();

        final JPAQuery<Long> query = new JPAQuery<>(em)
                .select(QAppActorNotification.appActorNotification.count())
                .from(QAppActorNotification.appActorNotification)
                .where(QAppActorNotification.appActorNotification.recipient.eq(currentActor))
                .where(QAppActorNotification.appActorNotification.read.isFalse());

        info.setTotalUnread(query.fetchOne());

        return info;
    }

    @Transactional
    public void changeReadStateOfPersonalNotification(UUID id, Boolean read) {
        ensureRecipientIsCurrentActor(id);
        final AppActorNotification notification = actorNotificationCrudService.getEntityById(id);
        notification.setRead(read);
        actorNotificationCrudService.saveEntity(notification);
    }
}
