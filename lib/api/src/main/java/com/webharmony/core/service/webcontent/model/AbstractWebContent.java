package com.webharmony.core.service.webcontent.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = BoxWebContent.class, name = "box"),
        @JsonSubTypes.Type(value = TitleWebContent.class, name = "title"),
        @JsonSubTypes.Type(value = TextWebContent.class, name = "text"),
        @JsonSubTypes.Type(value = ImageWebContent.class, name = "image")
})
public abstract class AbstractWebContent {

    @JsonProperty("type")
    @JsonIgnore
    public abstract String getType();

}
