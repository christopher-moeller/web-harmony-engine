package com.webharmony.core.data.jpa.model.webcontent;

import com.webharmony.core.data.jpa.model.files.AppFile;
import com.webharmony.core.data.jpa.model.utils.AbstractModificationInfoEntity;
import com.webharmony.core.utils.objects.ObjectsWithLabel;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "app_web_content")
public class AppWebContent extends AbstractModificationInfoEntity implements ObjectsWithLabel<String> {

    @Column(name = "label")
    private String label;

    @Lob
    @Column(name = "json_content", columnDefinition="TEXT")
    private String jsonContent;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "area")
    private AppWebContentArea area;

    @ManyToMany
    @JoinTable(
            name = "app_file__web_content",
            joinColumns = @JoinColumn(name = "web_content_id"),
            inverseJoinColumns = @JoinColumn(name = "file_id")
    )
    private Set<AppFile> files = new HashSet<>();

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }


}
