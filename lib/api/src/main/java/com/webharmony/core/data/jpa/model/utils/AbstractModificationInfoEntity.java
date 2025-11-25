package com.webharmony.core.data.jpa.model.utils;

import com.webharmony.core.context.ContextHolder;
import com.webharmony.core.service.data.ActorRightService;
import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@MappedSuperclass
public class AbstractModificationInfoEntity extends AbstractBaseEntity {

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "updated_by")
    private String updatedBy;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Override
    protected void prePersist() {
        super.prePersist();
        if(ContextHolder.isInitialized() && ContextHolder.getContext().getBean(ActorRightService.class).isInitialized())
            initializeModificationInfo();
    }

    @Override
    protected void preUpdate() {
        super.preUpdate();
        if(ContextHolder.isInitialized() && ContextHolder.getContext().getBean(ActorRightService.class).isInitialized())
            updateModificationInfo();
    }

    private void updateModificationInfo() {
        updatedAt = LocalDateTime.now();
        updatedBy = ContextHolder.getContext().getCurrentActor().getUniqueName();
    }

    private void initializeModificationInfo() {

        if(createdBy == null) {
            createdBy = ContextHolder.getContext().getCurrentActor().getUniqueName();
        } else {
            updatedBy = ContextHolder.getContext().getCurrentActor().getUniqueName();
        }

        if(createdAt == null) {
            createdAt = LocalDateTime.now();
        } else {
            updatedAt = LocalDateTime.now();
        }
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
