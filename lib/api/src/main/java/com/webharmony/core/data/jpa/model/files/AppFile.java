package com.webharmony.core.data.jpa.model.files;

import com.webharmony.core.data.jpa.model.utils.AbstractModificationInfoEntity;
import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "app_file")
public class AppFile extends AbstractModificationInfoEntity {

    @Column(name = "file_name")
    private String fileName;

    @Basic
    @Column(name = "file_as_byte", nullable = false)
    private byte[] fileAsByte;

    @Column(name = "is_temp", columnDefinition="BOOLEAN default FALSE")
    private Boolean isTemp  = false;

    @Column(name = "size")
    private long size;

    @Column(name = "web_type")
    private String webType;

    @Column(name = "type")
    private String type;

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

}
