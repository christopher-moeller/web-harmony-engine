package com.webharmony.core.api.rest.model.view.user;

import com.webharmony.core.api.rest.controller.user.UserRegistrationController;
import com.webharmony.core.api.rest.model.utils.ViewModel;
import com.webharmony.core.api.rest.model.utils.annotations.ViewModelLinks;
import com.webharmony.core.api.rest.model.utils.annotations.utils.ApiLinkSpecification;
import com.webharmony.core.api.rest.validation.SendUserRegistrationInvitationVMValidation;
import com.webharmony.core.service.data.validation.utils.Validation;
import com.webharmony.core.utils.reflection.ApiLink;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Validation(SendUserRegistrationInvitationVMValidation.class)
@ViewModelLinks(
        saveLink = SendUserRegistrationInvitationVM.SaveLink.class,
        loadLink = SendUserRegistrationInvitationVM.LoadLink.class
)
public class SendUserRegistrationInvitationVM implements ViewModel {

    private String email;
    private String message;

    public static final class SaveLink implements ApiLinkSpecification {

        @Override
        public ApiLink getLink() {
            return ApiLink.of(UserRegistrationController.class, c -> c.sendInvitation(null));
        }
    }

    public static final class LoadLink implements ApiLinkSpecification {

        @Override
        public ApiLink getLink() {
            return ApiLink.of(UserRegistrationController.class, UserRegistrationController::getRegistrationInvitationTemplate);
        }
    }

}
