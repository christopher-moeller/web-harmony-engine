package com.webharmony.core.api.rest.model.utils.validinput;

import com.webharmony.core.api.rest.model.utils.annotations.SelectableOptions;
import com.webharmony.core.api.rest.model.utils.optioncontainers.utils.AbstractSelectableOptionsContainer;
import com.webharmony.core.api.rest.model.utils.optioncontainers.utils.AbstractUnPagedOptionsContainer;
import com.webharmony.core.context.ContextHolder;
import com.webharmony.core.service.SelectableOptionContainerService;
import com.webharmony.core.utils.reflection.ApiLink;
import lombok.Getter;

import java.lang.reflect.Field;

@Getter
public class ApiLinkResourceSpecification extends ValidInputSpecification {

    private ApiLink apiLink;

    public ApiLinkResourceSpecification(Field field) {
        init(field);
    }

    private void init(Field field) {

        SelectableOptions selectableOptionsAnno = field.getDeclaredAnnotation(SelectableOptions.class);
        if(selectableOptionsAnno != null) {
            handleSelectableOptionsAnnotation(selectableOptionsAnno);
        }
    }

    private void handleSelectableOptionsAnnotation(SelectableOptions selectableOptions) {

        Class<? extends AbstractSelectableOptionsContainer<?>> containerClass = selectableOptions.value();
        if(AbstractUnPagedOptionsContainer.class.isAssignableFrom(containerClass)) {
            this.apiLink = ContextHolder.getContext().getBean(SelectableOptionContainerService.class).buildApiLinkForOptions(containerClass, false);
        }

    }

}
