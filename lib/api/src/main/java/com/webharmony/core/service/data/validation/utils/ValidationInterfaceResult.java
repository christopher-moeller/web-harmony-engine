package com.webharmony.core.service.data.validation.utils;

import com.webharmony.core.context.ContextHolder;
import com.webharmony.core.data.enums.ECoreActorRight;
import com.webharmony.core.i18n.I18N;
import com.webharmony.core.i18n.I18nTranslation;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class ValidationInterfaceResult<R> implements I18nTranslation {

    private final I18N i18N = createI18nInstance(ValidationInterfaceResult.class);

    private final R rootSource;
    private final String path;
    private final String validationName;

    @Getter
    private final List<ValidationError<R>> validationErrors = new ArrayList<>();

    public ValidationInterfaceResult(R rootSource, String path, final String validationName) {
        this.rootSource = rootSource;
        this.path = path;
        this.validationName = validationName;
    }

    public void extendResultByContext(ValidationContext<R> usedValidationContext) {
        this.validationErrors.addAll(usedValidationContext.getValidationErrors());
    }

    public void extendResultByException(Exception e) {
        final String message = isUserAllowedToSeeValidationMessage() ? e.getMessage() : i18N.translate("Unknown error").build();
        ValidationError<R> error = new ValidationError<>(rootSource, path, validationName, message, e.getStackTrace());
        validationErrors.add(error);
    }

    private boolean isUserAllowedToSeeValidationMessage() {
        return ContextHolder.getContext().currentActorAsRight(ECoreActorRight.CORE_ERROR_MESSAGE_FOR_VALIDATION_READ);
    }

}
