package com.webharmony.starter.api.rest.model;


import com.webharmony.core.api.rest.model.utils.ViewModel;
import com.webharmony.core.api.rest.model.utils.annotations.ViewModelLinks;
import com.webharmony.core.api.rest.model.utils.annotations.utils.ApiLinkSpecification;
import com.webharmony.core.service.data.validation.fieldvalidators.NotEmptyTextFieldValidator;
import com.webharmony.core.service.data.validation.utils.*;
import com.webharmony.core.utils.reflection.ApiLink;
import com.webharmony.starter.api.rest.controller.StarterController;
import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.nio.file.Path;

@Getter
@Setter
@ViewModelLinks(
        saveLink = CreateHarmonyProjectRequest.SaveLink.class,
        loadLink = CreateHarmonyProjectRequest.LoadLink.class
)
@Validation(CreateHarmonyProjectRequest.Validation.class)
public class CreateHarmonyProjectRequest implements ViewModel {

    private String projectShortName;
    private String projectLongName;
    private Boolean useH2Database;
    private String pathToProject;


    public static final class LoadLink implements ApiLinkSpecification {

        @Override
        public ApiLink getLink() {
            return ApiLink.of(StarterController.class, StarterController::getTemplate);
        }
    }

    public static final class SaveLink implements ApiLinkSpecification {

        @Override
        public ApiLink getLink() {
            return ApiLink.of(StarterController.class, c -> c.createProject(null));
        }
    }

    public static final class Validation implements ValidationConfigBuilder<CreateHarmonyProjectRequest> {

        @Override
        public void configureValidationBuilder(ValidationBuilder<CreateHarmonyProjectRequest, CreateHarmonyProjectRequest, ?, ? extends ValidationBuilder<CreateHarmonyProjectRequest, ?, ?, ?>> builder) {
            builder.ofField(CreateHarmonyProjectRequest::getProjectShortName).withValidation(new NotEmptyTextFieldValidator<>());
            builder.ofField(CreateHarmonyProjectRequest::getProjectLongName).withValidation(new NotEmptyTextFieldValidator<>());
            builder.ofField(CreateHarmonyProjectRequest::getPathToProject).withValidation("INVALID_PATH", this::hasValidPath);
        }

        private void hasValidPath(String value, ValidationContext<CreateHarmonyProjectRequest> context) {
            if (value == null || value.isEmpty()) {
                context.addValidationError("Path cannot be empty");
                return;
            }
            if (!value.startsWith("/")) {
                context.addValidationError("Path must start with '/'");
                return;
            }

            final Path path = Path.of(value);
            if (!path.isAbsolute()) {
                context.addValidationError("Path must be absolute");
                return;
            }

            final File filePath = path.toFile();
            if(filePath.exists() && !filePath.isDirectory()) {
                context.addValidationError("Path must be a directory");
            }
        }

    }
}
