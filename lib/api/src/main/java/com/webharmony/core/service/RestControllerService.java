package com.webharmony.core.service;

import com.webharmony.core.api.rest.controller.ApiController;
import com.webharmony.core.api.rest.controller.utils.AbstractBaseController;
import com.webharmony.core.api.rest.controller.utils.AbstractCrudController;
import com.webharmony.core.utils.assertions.Assert;
import com.webharmony.core.utils.exceptions.InternalServerException;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RestControllerService {

    private final List<AbstractBaseController> allRestControllers;

    public RestControllerService(List<AbstractBaseController> allRestControllers) {
        this.allRestControllers = allRestControllers;
        validateRestControllers();
    }

    private void validateRestControllers() {
        this.allRestControllers.forEach(this::validateRestController);
    }

    private void validateRestController(AbstractBaseController controller) {
        if(controller instanceof AbstractCrudController<?>)
            validateCrudController((AbstractCrudController<?>) controller);
    }

    private void validateCrudController(AbstractCrudController<?> crudController) {
        Assert.hasAnnotation(crudController.getClass(), ApiController.class)
                .withException(() -> new InternalServerException(String.format("@ApiController annotation is missing for '%s' but is required for all CRUD-Controllers", crudController.getClass())))
                .verify();

        String requestMappingValue = Optional.ofNullable(AnnotationUtils.findAnnotation(crudController.getClass(), ApiController.class))
                .map(ApiController::value)
                .map(a -> String.join(", ", a))
                .orElse(null);

        Assert.isNotEmpty(requestMappingValue)
                .withException(() -> new InternalServerException(String.format("@ApiController annotation for controller '%s' has an empty value", crudController.getClass())))
                .verify();

    }

}
