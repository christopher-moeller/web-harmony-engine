package com.webharmony.core.api.rest.model.view;

import com.webharmony.core.api.rest.controller.ApplicationStatusController;
import com.webharmony.core.api.rest.model.utils.ViewModel;
import com.webharmony.core.api.rest.model.utils.annotations.ViewModelLinks;
import com.webharmony.core.api.rest.model.utils.annotations.utils.ApiLinkSpecification;
import com.webharmony.core.api.rest.validation.ApplicationStatusVMValidation;
import com.webharmony.core.configuration.EApplicationStatus;
import com.webharmony.core.service.data.validation.utils.Validation;
import com.webharmony.core.utils.reflection.ApiLink;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Validation(ApplicationStatusVMValidation.class)
@ViewModelLinks(
        loadLink = ApplicationStatusVM.LoadLink.class,
        saveLink = ApplicationStatusVM.SaveLink.class
)
public class ApplicationStatusVM implements ViewModel {

    private EApplicationStatus status;
    private String userMessage;
    private String technicalMessage;


    public static final class LoadLink implements ApiLinkSpecification {

        @Override
        public ApiLink getLink() {
            return ApiLink.of(ApplicationStatusController.class, ApplicationStatusController::getCurrentStatus);
        }
    }

    public static final class SaveLink implements ApiLinkSpecification {

        @Override
        public ApiLink getLink() {
            return ApiLink.of(ApplicationStatusController.class, c -> c.saveCurrentStatus(null));
        }
    }
}
