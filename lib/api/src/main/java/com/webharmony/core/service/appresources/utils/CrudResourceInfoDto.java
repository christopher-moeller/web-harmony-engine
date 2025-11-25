package com.webharmony.core.service.appresources.utils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.webharmony.core.api.rest.model.utils.ComplexTypeSchema;
import com.webharmony.core.utils.reflection.ApiLink;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CrudResourceInfoDto extends CrudResourceInfoSimpleDto {

    @JsonProperty("schema")
    private ComplexTypeSchema complexTypeSchema;

    private ApiLink getAllLink;
    private ApiLink getByIdLink;
    private ApiLink createNewLink;
    private ApiLink updateLink;
    private ApiLink deleteLink;

    private ApiLink getTemplateLink;

    private ResourcePageableOptions pageableOptions;

    @JsonIgnore
    public CrudResourceInfoSimpleDto toSimpleDto() {
        CrudResourceInfoSimpleDto simple = new CrudResourceInfoSimpleDto();
        simple.setName(this.getName());
        return simple;
    }

}
