package com.webharmony.core.api.rest.model.view.user;

import com.webharmony.core.api.rest.controller.user.UserRegistrationController;
import com.webharmony.core.api.rest.model.utils.annotations.ReadOnlyAttribute;
import com.webharmony.core.api.rest.model.utils.annotations.ViewModelLinks;
import com.webharmony.core.api.rest.model.utils.annotations.utils.ApiLinkSpecification;
import com.webharmony.core.api.rest.validation.UserRegistrationWithInvitationVMValidation;
import com.webharmony.core.service.data.validation.utils.Validation;
import com.webharmony.core.utils.reflection.ApiLink;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Validation(UserRegistrationWithInvitationVMValidation.class)
@ViewModelLinks(
        saveLink = UserRegistrationWithInvitationVM.SaveLink.class,
        loadLink = UserRegistrationWithInvitationVM.LoadLink.class
)
public class UserRegistrationWithInvitationVM extends UserRegistrationVM {

    @ReadOnlyAttribute
    private String email;
    private String tokenValue;

    public static final class SaveLink implements ApiLinkSpecification {

        @Override
        public ApiLink getLink() {
            return ApiLink.of(UserRegistrationController.class, c -> c.updateRegistrationByInvitation(null));
        }
    }

    public static final class LoadLink implements ApiLinkSpecification {

        @Override
        public ApiLink getLink() {
            return ApiLink.of(UserRegistrationController.class, c -> c.getViewModelForRegistrationWithInvitation(null));
        }
    }
}
