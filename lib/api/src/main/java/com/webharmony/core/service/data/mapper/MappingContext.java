package com.webharmony.core.service.data.mapper;

import com.webharmony.core.api.rest.controller.utils.request.RequestContext;
import com.webharmony.core.context.ContextHolder;
import com.webharmony.core.i18n.EI18nLanguage;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationContext;

@Getter
public class MappingContext {

    private final ApplicationContext applicationContext;
    private final Object[] args;

    @Setter
    private boolean mapNestedEntityFields = true;

    private EI18nLanguage mappingLanguage;

    private MappingContext(ApplicationContext applicationContext, Object[] args) {
        this.applicationContext = applicationContext;
        this.args = args;
    }

    public static MappingContext ofEmptyRequestContext(Object... args) {
        return of(RequestContext.empty(ContextHolder.getContext().getSpringContext()), args);
    }

    public static MappingContext of(RequestContext requestContext, Object... args) {
        return new MappingContext(requestContext.getApplicationContext(), args);
    }

    public EI18nLanguage getMappingLanguage() {

        if(this.mappingLanguage == null) {
            initMappingLanguage();
        }

        return this.mappingLanguage;
    }

    private void initMappingLanguage() {
        this.mappingLanguage = ContextHolder.getContext().getContextLanguage();
    }

}
