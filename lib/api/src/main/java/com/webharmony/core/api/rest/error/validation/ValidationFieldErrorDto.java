package com.webharmony.core.api.rest.error.validation;

import com.webharmony.core.context.ContextHolder;
import com.webharmony.core.data.enums.ECoreActorRight;
import com.webharmony.core.service.data.validation.utils.ValidationError;
import com.webharmony.core.utils.exceptions.ExceptionHelper;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ValidationFieldErrorDto {

    private String fieldPath;
    private List<ValidationErrorMessageDto> errorMessages;

    public ValidationFieldErrorDto(String fieldPath, List<ValidationError<?>> validationErrors) {
        this.fieldPath = fieldPath;
        this.errorMessages = buildErrorMessages(validationErrors);
    }

    private List<ValidationErrorMessageDto> buildErrorMessages(List<ValidationError<?>> validationErrors) {
        return validationErrors.stream()
                .map(error -> new ValidationErrorMessageDto(error.validationName(), error.message(), getStacktraceOrNull(error)))
                .toList();
    }

    private String getStacktraceOrNull(ValidationError<?> validationError) {
        if(userHasRightToSeeStacktrace()) {
            String type = validationError.validationName();
            String message = validationError.message();
            return ExceptionHelper.buildStacktraceText(type, message, validationError.stackTraceElements());
        }

        return null;
    }

    private boolean userHasRightToSeeStacktrace() {
        return ContextHolder.getContext().currentActorAsRight(ECoreActorRight.CORE_ERROR_JAVA_STACKTRACE_READ);
    }
}
