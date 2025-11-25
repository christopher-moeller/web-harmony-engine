package com.webharmony.core.data.jpa.model.utils;

import com.webharmony.core.context.ContextHolder;
import com.webharmony.core.data.enums.utils.PersistenceEnum;
import com.webharmony.core.service.EntityService;
import com.webharmony.core.service.PersistentEnumService;
import com.webharmony.core.utils.exceptions.EntityNotFoundException;
import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@Getter
@Setter
@MappedSuperclass
public abstract class AbstractEnumEntity extends AbstractModificationInfoEntity implements EntityWithReadableId, CombinedCoreAndProjectEntityInstance {

    @Column(name = "unique_name")
    private String uniqueName;

    @Override
    public final String getReadableId() {
        return getUniqueName();
    }

    @Override
    protected void preUpdate() {
        super.preUpdate();
        this.executeValueChangeListenerIfDefined();
    }

    private void executeValueChangeListenerIfDefined() {
        if(!isNew()) {
            if(ContextHolder.isInitialized() && this.getUniqueName() != null) {
                final PersistenceEnum<? extends AbstractEnumEntity> persistenceEnum = ContextHolder.getContext().getBean(PersistentEnumService.class).findPersistenceEnumByEntity(this);
                persistenceEnum.executeOnValueChangedWithUntypedEntity(this);
            } else {
                log.error("Could not check if enum entity has value changed listener, because context is not initialized: {}:{}", this.getClass(), this.getReadableId());
            }
        }
    }

    public boolean reInitOnStartUp() {
        return false;
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean isInstanceFromCore() {
        if(this.uniqueName == null) {
            return false;
        }

        return ContextHolder.getContext().getBean(PersistentEnumService.class).isCreatedByCoreEnum(this);
    }

    @Override
    public EntityLoader getEntityLoader() {
        return (em, readableId) -> ContextHolder.getContext().getBean(EntityService.class).findEnumEntityByUniqueName(getClass(), readableId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Enum entity with class %s and unique name %s not found", getClass(), readableId)));
    }

    public boolean isCreatedByEnum() {
        return ContextHolder.getContext().getBean(PersistentEnumService.class).findPersistenceEnumByEntityIfExists(this).isPresent();
    }
}
