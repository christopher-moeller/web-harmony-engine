package com.webharmony.core.api.rest.error.validation;

import com.webharmony.core.service.data.validation.utils.ValidationError;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
public class ValidationResultDto {

    private List<ValidationFieldErrorDto> fields;

    public ValidationResultDto(List<ValidationError<?>> validationErrors) {
        this.fields = buildFieldErrors(validationErrors);
    }

    private List<ValidationFieldErrorDto> buildFieldErrors(List<ValidationError<?>> validationErrors) {
        return buildFieldErrorsMap(validationErrors)
                .entrySet()
                .stream()
                .map(e -> new ValidationFieldErrorDto(e.getKey(), e.getValue()))
                .toList();
    }

    private Map<String, List<ValidationError<?>>> buildFieldErrorsMap(List<ValidationError<?>> validationErrors) {
        Map<String, List<ValidationError<?>>> result = new HashMap<>();
        for(ValidationError<?> validationError : validationErrors) {
            result.putIfAbsent(validationError.path(), new ArrayList<>());
            result.get(validationError.path()).add(validationError);
        }
        return result;
    }
}
