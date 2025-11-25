package com.webharmony.core.service.webcontent.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public abstract class AbstractWebContentWithChildren extends AbstractWebContent {

    @JsonProperty("children")
    public abstract List<AbstractWebContent> getChildren();
}
