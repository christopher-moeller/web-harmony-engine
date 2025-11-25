package com.webharmony.core.data.jpa.model.i18n;

import com.webharmony.core.data.jpa.model.utils.AbstractBaseEntity;
import com.webharmony.core.i18n.EI18nLanguage;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Getter
@Setter
@Table(name = "app_i18n_entity_attribute")
@Entity
public class AppI18nEntityAttribute extends AbstractBaseEntity {

    @Column(name = "entity_class", nullable = false)
    private String entityClass;

    @Column(name = "attribute", nullable = false)
    private String attribute;

    @Column(name = "is_core_entry", nullable = false)
    private Boolean isCoreEntry;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "entityAttribute", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<AppI18nEntityAttributeValue> values = new HashSet<>();

    public Optional<String> getValueByLanguage(EI18nLanguage language) {
        return values.stream()
                .filter(a -> a.getLanguage().equals(language))
                .findAny()
                .map(AppI18nEntityAttributeValue::getTranslation);
    }

    public void putAttributeValue(EI18nLanguage language, String value) {
        Optional<AppI18nEntityAttributeValue> currentEntry = values.stream().filter(a -> a.getLanguage().equals(language)).findAny();
        if(currentEntry.isPresent()) {
            final AppI18nEntityAttributeValue currentValueEntry = currentEntry.get();
            currentValueEntry.setTranslation(value);
            currentValueEntry.setEntityAttribute(this);
        } else {
            final AppI18nEntityAttributeValue newEntry = new AppI18nEntityAttributeValue();
            newEntry.setLanguage(language);
            newEntry.setTranslation(value);
            newEntry.setEntityAttribute(this);
            values.add(newEntry);
        }
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
