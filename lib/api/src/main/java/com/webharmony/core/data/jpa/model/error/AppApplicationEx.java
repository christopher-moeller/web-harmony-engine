package com.webharmony.core.data.jpa.model.error;

import com.webharmony.core.data.jpa.model.utils.AbstractModificationInfoEntity;
import com.webharmony.core.utils.exceptions.utils.EApplicationErrorLocation;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "app_application_exception")
public class AppApplicationEx extends AbstractModificationInfoEntity {


    @Column(name = "exception_type")
    private String exceptionType;

    @Column(name = "message")
    private String message;

    @Column(name = "code_location")
    @Enumerated(EnumType.STRING)
    private EApplicationErrorLocation codeLocation;

    @Column(name = "description", length = 1000)
    private String description;

    @Lob
    @Column(name = "stacktrace", columnDefinition="TEXT")
    private String stacktrace;

    @Lob
    @Column(name = "log", columnDefinition="TEXT")
    private String log;

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
