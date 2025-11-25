package com.webharmony.core.data.jpa.model.actor;

import com.webharmony.core.data.jpa.model.utils.AbstractBaseEntity;
import com.webharmony.core.configuration.security.ApplicationRight;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Getter
@Setter
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="actor_type",
        discriminatorType = DiscriminatorType.STRING)

@Table(name = "app_actor")
@Entity
public abstract class AbstractActor extends AbstractBaseEntity {

    @Column(name = "unique_name", unique = true)
    private String uniqueName;

    @Transient
    private final Set<ApplicationRight> effectiveRights = new HashSet<>();

    public void initRights() {
        effectiveRights.clear();
        effectiveRights.addAll(buildEffectiveRights());
    }

    protected abstract Set<ApplicationRight> buildEffectiveRights();

    public boolean hasRight(ApplicationRight applicationRight) {
        return effectiveRights.contains(applicationRight);
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    public Optional<String> getActorFirstname() {
        if(this instanceof AppUserActor userActor) {
            return Optional.ofNullable(userActor.getUser().getFirstname());
        } else {
            return Optional.empty();
        }
    }
}
