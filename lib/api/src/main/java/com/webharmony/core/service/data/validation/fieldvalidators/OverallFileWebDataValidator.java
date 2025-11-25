package com.webharmony.core.service.data.validation.fieldvalidators;

import com.webharmony.core.api.rest.model.utils.FileWebData;
import com.webharmony.core.service.data.FileService;
import com.webharmony.core.service.data.validation.utils.NamedValidationInterface;
import com.webharmony.core.service.data.validation.utils.ValidationContext;
import com.webharmony.core.utils.exceptions.FileWebDataValidationException;

import java.util.List;

public class OverallFileWebDataValidator<R> implements NamedValidationInterface<List<FileWebData>, R> {

    public static final String NAME = "OVERALL_FILE_WEB_DATA_VALIDATOR";

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public void validate(List<FileWebData> webData, ValidationContext<R> validationContext) {
        try {
            validationContext.getBean(FileService.class).validateOverallSizeOfFileWebData(webData);
        } catch (FileWebDataValidationException e) {
            validationContext.addValidationError(e.getMessage());
        }
    }
}
