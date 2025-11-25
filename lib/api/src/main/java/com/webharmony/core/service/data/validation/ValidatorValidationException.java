package com.webharmony.core.service.data.validation;

import com.webharmony.core.i18n.ECoreI18nStaticTranslations;
import com.webharmony.core.service.data.validation.utils.ValidationError;
import com.webharmony.core.utils.exceptions.ApplicationException;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.List;

@Getter
public class ValidatorValidationException extends ApplicationException {

    private final transient Object validationObject;
    private final transient List<ValidationError<?>> validationErrors;

    public ValidatorValidationException(Object validationObject, List<ValidationError<?>> validationErrors) {
        super(buildMessage(validationObject, validationErrors));
        this.validationObject = validationObject;
        this.validationErrors = validationErrors;
    }

    private static String buildMessage(Object validationObject, List<ValidationError<?>> validationErrors) {
        return ECoreI18nStaticTranslations.VALIDATOR_VALIDATION_ERROR.getI18n().add("countOfFailedValidations", validationErrors.size()).add("validationType", validationObject.getClass().getName()).build();
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.BAD_REQUEST;
    }

    @Override
    public boolean shouldLogException() {
        return false;
    }
}
