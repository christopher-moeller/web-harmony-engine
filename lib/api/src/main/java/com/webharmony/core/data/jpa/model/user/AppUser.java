package com.webharmony.core.data.jpa.model.user;

import com.querydsl.jpa.impl.JPAQuery;
import com.webharmony.core.data.jpa.model.utils.AbstractModificationInfoEntity;
import com.webharmony.core.data.jpa.model.utils.EntityWithReadableId;
import com.webharmony.core.utils.exceptions.MethodNotAllowedException;
import com.webharmony.core.utils.objects.ObjectsWithLabel;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "app_user")
public class AppUser extends AbstractModificationInfoEntity implements EntityWithReadableId, ObjectsWithLabel<String> {

    @Column(name = "email")
    private String email;

    @Column(name = "password")
    private String password;

    @Column(name = "firstname")
    private String firstname;

    @Column(name = "lastname")
    private String lastname;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "user_access_config", referencedColumnName = "uuid")
    private AppUserAccessConfig userAccessConfig;

    @Override
    public String getReadableId() {
        return getEmail();
    }

    @Override
    public EntityLoader getEntityLoader() {
        return (em, readableId) -> new JPAQuery<>(em).select(QAppUser.appUser)
                .from(QAppUser.appUser)
                .where(QAppUser.appUser.email.eq(email))
                .fetchOne();
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
    public String getLabel() {
        return firstname;
    }

    @Override
    public void setLabel(String label) {
        throw new MethodNotAllowedException();
    }
}
