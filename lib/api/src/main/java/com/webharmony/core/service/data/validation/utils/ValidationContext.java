package com.webharmony.core.service.data.validation.utils;

import com.webharmony.core.utils.StringUtils;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationContext;

import java.util.ArrayList;
import java.util.List;

@Getter
public class ValidationContext<R> {

    @Setter
    private String path;
    private final R rootSource;
    private final String validationName;
    private final ApplicationContext applicationContext;

    @Getter
    private final List<ValidationError<R>> validationErrors = new ArrayList<>();


    public ValidationContext(String path, R rootSource, String validationName, ApplicationContext applicationContext) {
        this.path = path;
        this.rootSource = rootSource;
        this.validationName = validationName;
        this.applicationContext = applicationContext;
    }

    public void addValidationError(String message) {
        ValidationError<R> error = new ValidationError<>(rootSource, path, validationName, message, Thread.currentThread().getStackTrace());
        validationErrors.add(error);
    }

    public void addValidationErrorForSubPath(String subPath, String message) {
        final String fullPath = StringUtils.isNullOrEmpty(path) ? subPath : String.join(".", path, subPath);
        ValidationError<R> error = new ValidationError<>(rootSource, fullPath, validationName, message, Thread.currentThread().getStackTrace());
        validationErrors.add(error);
    }

    public <B> B getBean(Class<B> beanClass) {
        return applicationContext.getBean(beanClass);
    }
}
