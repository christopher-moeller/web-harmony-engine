package com.webharmony.core.data.jpa.model.user;

import com.webharmony.core.data.jpa.model.i18n.AppI18nEntityAttribute;
import com.webharmony.core.data.jpa.model.utils.AbstractEnumEntity;
import com.webharmony.core.utils.objects.ObjectsWithLabel;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "app_user_role")
public class AppUserRole extends AbstractEnumEntity implements ObjectsWithLabel<AppI18nEntityAttribute> {

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "label")
    private AppI18nEntityAttribute label;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "description")
    private AppI18nEntityAttribute description;

    @ManyToMany
    @JoinTable(
            name = "app_rights_roles",
            joinColumns = @JoinColumn(name = "role_id"),
            inverseJoinColumns = @JoinColumn(name = "right_id")
    )
    private Set<AppActorRight> includedRights = new HashSet<>();

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }


}
