package com.webharmony.core.service.data.validation.fieldvalidators;

import com.webharmony.core.api.rest.model.utils.annotations.SelectableOptions;
import com.webharmony.core.api.rest.model.utils.optioncontainers.utils.AbstractSelectableOptionsContainer;
import com.webharmony.core.i18n.I18N;
import com.webharmony.core.i18n.I18nTranslation;
import com.webharmony.core.service.SelectableOptionContainerService;
import com.webharmony.core.service.data.validation.utils.NamedValidationInterface;
import com.webharmony.core.service.data.validation.utils.ValidationContext;
import com.webharmony.core.utils.exceptions.InternalServerException;
import com.webharmony.core.utils.reflection.ReflectionUtils;
import org.springframework.core.annotation.AnnotationUtils;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Optional;

public class SelectableOptionValidator<R, O> implements NamedValidationInterface<O, R>, I18nTranslation {

    private final I18N i18N = createI18nInstance(SelectableOptionValidator.class);

    public static final String NAME = "SELECTABLE_OPTION_FIELD_VALIDATOR";

    @Override
    public String getName() {
        return NAME;
    }

    private final boolean isRequired;

    public SelectableOptionValidator(boolean isRequired) {
        this.isRequired = isRequired;
    }

    @Override
    public void validate(O option, ValidationContext<R> validationContext) {

        if(option == null) {
            if(isRequired) {
                validationContext.addValidationError(i18N.translate("Selection is required").build());
            }

            return;
        }

        SelectableOptions selectableOptionsAnnotation = findSelectableOptionsAnnotationByPath(validationContext.getRootSource().getClass(), validationContext.getPath())
                .orElse(null);

        if(selectableOptionsAnnotation == null)
            return;

        AbstractSelectableOptionsContainer<?> optionsContainer = validationContext.getApplicationContext()
                .getBean(SelectableOptionContainerService.class)
                .getOptionsContainerByClass(selectableOptionsAnnotation.value());

        if(option instanceof Collection<?> optionAsCollection) {
            optionAsCollection.forEach(o -> validateInternal(o, optionsContainer, validationContext, true));
        } else {
            validateInternal(option, optionsContainer, validationContext, false);
        }
    }

    private void validateInternal(Object option, AbstractSelectableOptionsContainer<?> optionsContainer, ValidationContext<R> validationContext, boolean isCollection) {
        if(!optionsContainer.untypedOptionIsInRange(option)) {
            final String message = isCollection ? i18N.translate("At least one option is not allowed").build() : i18N.translate("Option is not allowed").build();
            validationContext.addValidationError(message);
        }
    }

    private Optional<SelectableOptions> findSelectableOptionsAnnotationByPath(Class<?> rootClass, String path) {
        Field field = ReflectionUtils.getFieldByPath(rootClass, path);
        if(field == null)
            throw new InternalServerException(i18N.translate("Field not found").build());
        return Optional.ofNullable(AnnotationUtils.findAnnotation(field, SelectableOptions.class));
    }
}
