package com.webharmony.core.api.rest.model.error;

import com.webharmony.core.api.rest.model.utils.AbstractResourceDto;
import com.webharmony.core.api.rest.model.utils.FileWebData;
import com.webharmony.core.api.rest.validation.ReportedUserErrorRequestValidation;
import com.webharmony.core.service.data.validation.utils.Validation;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Validation(ReportedUserErrorRequestValidation.class)
public class ReportedUserErrorDto extends AbstractResourceDto {

    private String page;
    private String description;
    private List<FileWebData> attachments = new ArrayList<>();

}
