package com.webharmony.core.api.rest.model;

import com.webharmony.core.api.rest.model.utils.AbstractResourceDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ActorDto extends AbstractResourceDto {

    private String type;
    private String uniqueName;
    private String userId;

}
