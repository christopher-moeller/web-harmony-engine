package com.webharmony.core.api.rest.model.error;

import com.webharmony.core.api.rest.model.utils.BaseDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FrontendRuntimeError extends BaseDto {

    private String message;
    private String url;
    private Integer line;
    private Integer col;
    private String error;

}
