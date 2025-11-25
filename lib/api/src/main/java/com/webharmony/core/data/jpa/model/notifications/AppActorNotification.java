package com.webharmony.core.data.jpa.model.notifications;

import com.webharmony.core.data.jpa.model.actor.AbstractActor;
import com.webharmony.core.data.jpa.model.utils.AbstractModificationInfoEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "app_actor_notification")
public class AppActorNotification extends AbstractModificationInfoEntity {

    @Column(name = "caption", nullable = false)
    private String caption;

    @Column(name = "read", columnDefinition = "boolean default false")
    private boolean read = false;

    @Lob
    @Column(name = "text_message", columnDefinition="TEXT")
    private String textMessage;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "recipient")
    private AbstractActor recipient;

    @Lob
    @Column(name = "payload", columnDefinition="TEXT")
    private String payload;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "event_type", nullable = false)
    private AppActorNotificationEventType eventType;

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
