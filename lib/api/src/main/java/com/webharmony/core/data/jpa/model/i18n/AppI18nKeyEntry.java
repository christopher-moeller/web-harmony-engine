package com.webharmony.core.data.jpa.model.i18n;

import com.webharmony.core.data.jpa.model.utils.AbstractBaseEntity;
import com.webharmony.core.i18n.EI18nCodeLocation;
import com.webharmony.core.service.i18n.I18nKeyEntryService;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "app_i18n_key_entry", uniqueConstraints = { @UniqueConstraint(name = "ClassIdAndKey", columnNames = { "class_id", "i18n_key" }) })
public class AppI18nKeyEntry extends AbstractBaseEntity {

    @Column(name = "class_id")
    private String classId;

    @Column(name = "i18n_key")
    private String key;

    @Column(name = "placeholders")
    private String placeholders;

    @Column(name = "description")
    private String description;

    @Column(name = "code_location")
    @Enumerated(EnumType.STRING)
    private EI18nCodeLocation codeLocation;

    @Lob
    @Column(name = "code_lines")
    private String codeLines;

    @Column(name = "is_core_entry", nullable = false)
    private Boolean isCoreEntry;

    @OneToMany(mappedBy="i18nKeyEntry", fetch = FetchType.EAGER, orphanRemoval = true, cascade = CascadeType.ALL)
    private Set<AppI18nTranslation> translations;

    public String getReadableIdRepresentation() {
        return I18nKeyEntryService.buildReadableId(classId, key);
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }


}
