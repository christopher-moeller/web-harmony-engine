package com.webharmony.core.data.jpa.model.utils;

import jakarta.persistence.EntityManager;

public interface EntityWithReadableId {

    String getReadableId();

    EntityLoader getEntityLoader();

    interface EntityLoader {
        EntityWithReadableId findEntityByReadableId(EntityManager em, String readableId);
    }

}
