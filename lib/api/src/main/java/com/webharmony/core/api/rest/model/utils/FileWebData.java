package com.webharmony.core.api.rest.model.utils;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.webharmony.core.api.rest.jackson.CustomLocalDateTimeDeserializer;
import com.webharmony.core.api.rest.jackson.CustomLocalDateTimeSerializer;
import com.webharmony.core.utils.reflection.ApiLink;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class FileWebData {

    private String id;
    private String name;
    private String type;
    private Long size;
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime lastModified;
    private String base64Content;
    @JsonProperty(value = "downloadLink", access = JsonProperty.Access.READ_ONLY)
    private ApiLink downloadLink;
    private String simpleDownloadLink;

}
