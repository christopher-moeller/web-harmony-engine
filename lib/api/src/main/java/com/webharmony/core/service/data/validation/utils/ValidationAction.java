package com.webharmony.core.service.data.validation.utils;

import java.util.List;

public record ValidationAction<R>(R rootSource, String path, Object target,
                                     List<NamedValidationInterface<?, R>> validationInterfaces) {

}
