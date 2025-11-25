package com.webharmony.core.data.jpa.model.userregistration;

import com.webharmony.core.data.jpa.model.utils.AbstractModificationInfoEntity;
import com.webharmony.core.data.jpa.utils.SQLXMLType;
import com.webharmony.core.service.userregistration.EUserRegistrationState;
import com.webharmony.core.service.userregistration.EUserRegistrationWorkflow;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;

@Getter
@Setter
@Entity
@Table(name = "app_user_registration")
public class AppUserRegistration extends AbstractModificationInfoEntity {

    @Column(name = "email")
    private String email;

    @Column(name = "state")
    @Enumerated(EnumType.STRING)
    private EUserRegistrationState state;

    @Column(name = "workflow")
    @Enumerated(EnumType.STRING)
    private EUserRegistrationWorkflow workflow;

    @Column(name = "state_data")
    @Type(SQLXMLType.class)
    private NestedUserRegistrationStateDataField stateData;

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
