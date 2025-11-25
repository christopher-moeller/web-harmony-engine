package com.webharmony.core.data.jpa.model;

import com.webharmony.core.data.jpa.model.i18n.AppI18nEntityAttribute;
import com.webharmony.core.data.jpa.model.utils.AbstractEnumEntity;
import com.webharmony.core.utils.StringUtils;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;

@Getter
@Setter
@Entity
@Table(name = "app_registry_item")
public class AppRegistryItem extends AbstractEnumEntity {

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "label")
    private AppI18nEntityAttribute label;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "description")
    private AppI18nEntityAttribute description;

    @Column(name = "value_as_string")
    private String valueAsString;

    @Column(name = "java_type")
    private String javaType;

    @Override
    public boolean reInitOnStartUp() {
        return label == null || description == null;
    }

    @SneakyThrows
    public Class<?> getJavaTypeClass() {
        return Class.forName(javaType);
    }

    @SneakyThrows
    public Object getValue() {
        return StringUtils.mapToObject(valueAsString, Class.forName(javaType));
    }

    public void setValue(Object value) {
        this.valueAsString = StringUtils.mapToString(value);
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
