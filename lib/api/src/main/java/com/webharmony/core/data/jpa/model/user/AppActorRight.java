package com.webharmony.core.data.jpa.model.user;

import com.webharmony.core.data.jpa.model.i18n.AppI18nEntityAttribute;
import com.webharmony.core.data.jpa.model.utils.AbstractEnumEntity;
import com.webharmony.core.utils.objects.ObjectsWithLabel;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "app_actor_right")
public class AppActorRight extends AbstractEnumEntity implements ObjectsWithLabel<AppI18nEntityAttribute> {

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "label")
    private AppI18nEntityAttribute label;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "description")
    private AppI18nEntityAttribute description;

    @Column(name = "is_allowed_for_system_actor", columnDefinition="BOOLEAN default TRUE")
    private Boolean isAllowedForSystemActor = true;

    @Column(name = "is_allowed_for_unknown_public_actor", columnDefinition="BOOLEAN default FALSE")
    private Boolean isAllowedForUnknownPublicActor = false;

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

}
