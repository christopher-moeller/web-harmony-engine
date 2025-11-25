package com.webharmony.core.service.appviewmodels;

import com.webharmony.core.api.rest.model.utils.ComplexTypeSchema;
import com.webharmony.core.utils.reflection.ApiLink;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ViewModelInfoDto {

    private String viewModelName;
    private ComplexTypeSchema schema;

    private ApiLink loadLink;
    private ApiLink saveLink;

}
