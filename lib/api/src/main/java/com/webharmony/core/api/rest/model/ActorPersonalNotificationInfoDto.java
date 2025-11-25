package com.webharmony.core.api.rest.model;

import com.webharmony.core.api.rest.model.utils.BaseDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ActorPersonalNotificationInfoDto extends BaseDto {

    private Long totalUnread;

}
