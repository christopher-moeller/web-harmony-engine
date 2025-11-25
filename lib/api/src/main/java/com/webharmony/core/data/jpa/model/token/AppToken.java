package com.webharmony.core.data.jpa.model.token;

import com.webharmony.core.data.jpa.model.utils.AbstractModificationInfoEntity;
import com.webharmony.core.utils.JacksonUtils;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "app_token")
public class AppToken extends AbstractModificationInfoEntity {

    @Column(name = "token_value")
    private String tokenValue;

    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private ETokenType type;

    @Lob
    @Column(name = "payload", columnDefinition="TEXT")
    private String payload;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    @SneakyThrows
    public <T> T getTypedPayload(Class<T> type) {
        if(this.payload == null)
            return null;

        return JacksonUtils.createDefaultJsonMapper().readValue(this.payload, type);
    }

    @SneakyThrows
    public void setTypedPayload(Object value) {
        if(value == null)
            this.payload = null;
        else
            this.payload = JacksonUtils.createDefaultJsonMapper().writeValueAsString(value);
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
