package com.webharmony.core.data.jpa.model.error;

import com.webharmony.core.data.jpa.model.files.AppFile;
import com.webharmony.core.data.jpa.model.utils.AbstractModificationInfoEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "app_reported_user_error")
public class AppReportedUserError extends AbstractModificationInfoEntity {

    @Column(name = "page")
    private String page;

    @Column(name = "description", length = 1000)
    private String description;

    @ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "app_file_app_reported_user_error",
            joinColumns = @JoinColumn(name = "reported_user_error_id"),
            inverseJoinColumns = @JoinColumn(name = "file_id")
    )
    private Set<AppFile> attachments = new HashSet<>();

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
