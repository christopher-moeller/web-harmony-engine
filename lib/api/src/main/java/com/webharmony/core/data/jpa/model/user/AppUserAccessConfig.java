package com.webharmony.core.data.jpa.model.user;

import com.webharmony.core.data.jpa.model.utils.AbstractModificationInfoEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "app_user_access_config")
public class AppUserAccessConfig extends AbstractModificationInfoEntity {

    @Column(name = "is_admin")
    private Boolean isAdmin;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "main_role")
    private AppUserRole mainRole;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "app_roles_user_access",
            joinColumns = @JoinColumn(name = "user_access_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<AppUserRole> roles = new HashSet<>();

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "app_rights_user_access",
            joinColumns = @JoinColumn(name = "user_access_id"),
            inverseJoinColumns = @JoinColumn(name = "right_id")
    )
    private Set<AppActorRight> additionalRights = new HashSet<>();

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

}
