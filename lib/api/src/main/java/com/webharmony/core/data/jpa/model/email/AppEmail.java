package com.webharmony.core.data.jpa.model.email;

import com.webharmony.core.data.jpa.model.files.AppFile;
import com.webharmony.core.data.jpa.model.utils.AbstractModificationInfoEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@Table(name = "app_email")
public class AppEmail extends AbstractModificationInfoEntity {

    @Column(name = "state")
    @Enumerated(EnumType.STRING)
    private EMailState state;

    @Column(name = "from_email")
    private String fromEmail;

    @Column(name = "to_email")
    private String toEmail;

    @Column(name = "subject")
    private String subject;

    @Lob
    @Column(name = "html_message", columnDefinition="TEXT")
    private String htmlMessage;

    @Lob
    @Column(name = "event_log", columnDefinition="TEXT")
    private String eventLog;

    @Column(name = "last_sending")
    private LocalDateTime lastSending;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "app_file_app_email",
            joinColumns = @JoinColumn(name = "email_id"),
            inverseJoinColumns = @JoinColumn(name = "file_id")
    )
    private Set<AppFile> attachments = new HashSet<>();

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
