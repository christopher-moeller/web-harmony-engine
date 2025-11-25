package com.webharmony.core.data.jpa.model.harmonyevent;

import com.webharmony.core.data.jpa.model.utils.AbstractModificationInfoEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "app_harmony_event")
public class AppHarmonyEvent extends AbstractModificationInfoEntity {

    @Column(name = "java_type")
    private String javaType;

    @Lob
    @Column(name = "payload", columnDefinition="TEXT")
    private String payload;

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
