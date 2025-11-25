package com.webharmony.core.api.rest.model;

import com.webharmony.core.api.rest.model.utils.AbstractResourceDto;
import com.webharmony.core.api.rest.model.utils.annotations.ReadOnlyAttribute;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ActorNotificationEventTypeDto extends AbstractResourceDto {

    @ReadOnlyAttribute
    private String label;
    private String description;
    private Boolean isActive;

}
