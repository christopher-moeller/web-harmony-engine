package com.webharmony.core.data.jpa.model.i18n;

import com.webharmony.core.data.jpa.model.utils.AbstractBaseEntity;
import com.webharmony.core.i18n.EI18nLanguage;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "app_i18n_translation", uniqueConstraints = { @UniqueConstraint(name = "entryAndLanguage", columnNames = { "i18n_key_entry", "language" }) })
public class AppI18nTranslation extends AbstractBaseEntity {

    @Column(name = "language")
    @Enumerated(EnumType.STRING)
    private EI18nLanguage language;

    @Column(name = "translation")
    private String translation;

    @ManyToOne
    @JoinColumn(name = "i18n_key_entry")
    private AppI18nKeyEntry i18nKeyEntry;

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
