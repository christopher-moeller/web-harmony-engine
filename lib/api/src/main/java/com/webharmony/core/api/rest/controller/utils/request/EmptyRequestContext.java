package com.webharmony.core.api.rest.controller.utils.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.context.ApplicationContext;

@Getter
@AllArgsConstructor
public class EmptyRequestContext implements RequestContext {

    private final ApplicationContext applicationContext;
}
