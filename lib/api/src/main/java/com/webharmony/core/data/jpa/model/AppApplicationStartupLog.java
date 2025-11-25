package com.webharmony.core.data.jpa.model;

import com.webharmony.core.data.jpa.model.utils.AbstractBaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "app_application_startup_log")
public class AppApplicationStartupLog extends AbstractBaseEntity {


    @Column(name = "started_at", nullable = false)
    private LocalDateTime startedAt;

    @Column(name = "duration_in_millis", nullable = false)
    private long durationInMillis;

    @Column(name = "duration_text", nullable = false)
    private String durationText;

}
