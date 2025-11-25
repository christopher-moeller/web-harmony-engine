package com.webharmony.core.api.rest.model;

import com.webharmony.core.api.rest.model.utils.AbstractResourceDto;
import com.webharmony.core.api.rest.model.utils.annotations.ReadOnlyAttribute;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ActorNotificationDto extends AbstractResourceDto {

    @ReadOnlyAttribute
    private String sendAt;
    @ReadOnlyAttribute
    private String caption;
    @ReadOnlyAttribute
    private Boolean read = false;
    @ReadOnlyAttribute
    private String textMessage;
    @ReadOnlyAttribute
    private String recipient;
    @ReadOnlyAttribute
    private String payload;
    @ReadOnlyAttribute
    private ActorNotificationEventTypeDto eventType;

}
