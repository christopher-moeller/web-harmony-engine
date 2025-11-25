package com.webharmony.core.data.jpa.model;

import com.webharmony.core.data.jpa.model.utils.AbstractModificationInfoEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "app_server_task")
public class AppServerTask extends AbstractModificationInfoEntity {

    @Column(name = "task_id")
    private String taskId;

    @Column(name = "task_name")
    private String taskName;

    @Column(name = "last_execution")
    private LocalDateTime lastExecution;

    @Column(name = "is_required")
    private Boolean isRequired;

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
