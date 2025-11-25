package com.webharmony.core.service.data.validation.fieldvalidators;

import com.webharmony.core.api.rest.model.utils.FileWebData;
import com.webharmony.core.service.data.FileService;
import com.webharmony.core.service.data.validation.utils.NamedValidationInterface;
import com.webharmony.core.service.data.validation.utils.ValidationContext;
import com.webharmony.core.utils.exceptions.FileWebDataValidationException;
import org.springframework.stereotype.Component;

@Component
public class SingleFileWebDataValidator<R> implements NamedValidationInterface<FileWebData, R> {

    public static final String NAME = "FILE_WEB_DATA_VALIDATOR";

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public void validate(FileWebData value, ValidationContext<R> validationContext) {
        try {
            validationContext.getBean(FileService.class).validateSizeOfFileWebData(value);
        } catch (FileWebDataValidationException e) {
            validationContext.addValidationError(e.getMessage());
        }
    }
}
