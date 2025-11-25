package com.webharmony.core.data.jpa.model.i18n;

import com.webharmony.core.data.jpa.model.utils.AbstractBaseEntity;
import com.webharmony.core.i18n.EI18nLanguage;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
@Table(name = "app_i18n_entity_attribute_value")
@Entity
public class AppI18nEntityAttributeValue extends AbstractBaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "entity_attribute", nullable = false)
    private AppI18nEntityAttribute entityAttribute;

    @Column(name = "language", nullable = false)
    @Enumerated(EnumType.STRING)
    private EI18nLanguage language;

    @Column(name = "translation")
    private String translation;

    @Override
    public boolean equals(Object o) {
        return super.equals(o) && Objects.equals(language, ((AppI18nEntityAttributeValue) o).getLanguage());
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
