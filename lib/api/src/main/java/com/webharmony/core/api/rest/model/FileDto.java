package com.webharmony.core.api.rest.model;

import com.webharmony.core.api.rest.model.utils.AbstractResourceDto;
import com.webharmony.core.api.rest.validation.FileDtoValidation;
import com.webharmony.core.service.data.validation.utils.Validation;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Validation(FileDtoValidation.class)
public class FileDto extends AbstractResourceDto {

    private String fileName;

    private Boolean isTemp;
}
