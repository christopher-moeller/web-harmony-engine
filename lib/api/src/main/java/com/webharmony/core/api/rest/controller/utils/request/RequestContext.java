package com.webharmony.core.api.rest.controller.utils.request;

import com.webharmony.core.api.rest.controller.utils.RestRequestParams;
import org.springframework.context.ApplicationContext;

public interface RequestContext {

    static EmptyRequestContext empty(ApplicationContext applicationContext) {
        return new EmptyRequestContext(applicationContext);
    }

    static SearchRequestContext search(ApplicationContext applicationContext, RestRequestParams restRequestParams) {
        return new SearchRequestContext(applicationContext, restRequestParams);
    }

    ApplicationContext getApplicationContext();
}
