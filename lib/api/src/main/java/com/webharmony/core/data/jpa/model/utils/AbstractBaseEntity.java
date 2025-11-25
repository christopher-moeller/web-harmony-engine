package com.webharmony.core.data.jpa.model.utils;

import com.webharmony.core.context.ContextHolder;
import com.webharmony.core.service.i18n.I18nEntityAttributeService;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
@MappedSuperclass
public abstract class AbstractBaseEntity {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(nullable = false, unique = true, columnDefinition = "uuid", name = "uuid")
    private UUID uuid;

    @Transient
    private UUID localTransientUuid;

    public String getIDRepresentation() {
        if(this instanceof EntityWithReadableId entityWithReadableId)
            return entityWithReadableId.getReadableId();
        else
            return String.valueOf(uuid);
    }

    @PrePersist
    protected void prePersist() {
        initializeUUID();
        initializeI18nAttributes();
    }

    @PreUpdate
    protected void preUpdate() {

    }

    public void initializeUUID() {
        if(uuid == null)
            uuid = UUID.randomUUID();
    }

    public void initializeLocalTransientUuid() {
        if(localTransientUuid == null)
            localTransientUuid = UUID.randomUUID();
    }

    public void initializeI18nAttributes() {
        ContextHolder.getSpringContext().getBean(I18nEntityAttributeService.class)
                .initializeAttributesForEntity(this);
    }

    public boolean isNew() {
        return uuid == null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AbstractBaseEntity that = (AbstractBaseEntity) o;
        return Objects.equals(uuid, that.uuid) && Objects.equals(localTransientUuid, that.localTransientUuid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid, localTransientUuid);
    }
}
