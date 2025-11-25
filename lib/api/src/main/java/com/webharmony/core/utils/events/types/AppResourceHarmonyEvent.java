package com.webharmony.core.utils.events.types;

import com.webharmony.core.api.rest.model.utils.AbstractResourceDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AppResourceHarmonyEvent extends HarmonyEvent {

    public static final String CREATED = "CREATED";
    public static final String UPDATED = "UPDATED";
    public static final String DELETED = "DELETED";

    private String resourceName;

    private String resourceId;

    private String action;

    private AbstractResourceDto payload;

}
