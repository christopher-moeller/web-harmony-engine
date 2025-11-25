package com.webharmony.core.data.enums.utils;

import com.webharmony.core.context.ContextHolder;
import com.webharmony.core.data.jpa.model.utils.AbstractEnumEntity;
import com.webharmony.core.service.PersistentEnumService;

import java.util.Optional;
import java.util.function.Consumer;

public interface PersistenceEnum<E extends AbstractEnumEntity> {

    String name();

    default E getEntity() {
        final PersistentEnumService persistentEnumService = ContextHolder.getSpringContext().getBean(PersistentEnumService.class);
        return persistentEnumService.loadEntityByEnum(this);
    }

    @SuppressWarnings("unchecked")
    default void initUntypedEntity(AbstractEnumEntity enumEntity) {
        initEntity((E) enumEntity);
    }

    void initEntity(E entity);

    Class<E> getEntityClass();

    default Consumer<E> getOnValueChangedConsumer() {
        return null;
    }

    @SuppressWarnings("unchecked")
    default void executeOnValueChangedWithUntypedEntity(AbstractEnumEntity enumEntity) {
        Optional.ofNullable(getOnValueChangedConsumer()).ifPresent(consumer -> consumer.accept((E) enumEntity));
    }

}
