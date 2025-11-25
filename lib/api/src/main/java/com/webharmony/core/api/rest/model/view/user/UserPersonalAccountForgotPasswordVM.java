package com.webharmony.core.api.rest.model.view.user;

import com.webharmony.core.api.rest.controller.user.UserController;
import com.webharmony.core.api.rest.model.utils.ViewModel;
import com.webharmony.core.api.rest.model.utils.annotations.ViewModelLinks;
import com.webharmony.core.api.rest.model.utils.annotations.utils.ApiLinkSpecification;
import com.webharmony.core.api.rest.validation.UserPersonalAccountForgotPasswordVMValidation;
import com.webharmony.core.service.data.validation.utils.Validation;
import com.webharmony.core.utils.reflection.ApiLink;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Validation(UserPersonalAccountForgotPasswordVMValidation.class)
@ViewModelLinks(
        saveLink = UserPersonalAccountForgotPasswordVM.SaveLink.class
)
public class UserPersonalAccountForgotPasswordVM implements ViewModel {

    private String email;

    public static final class SaveLink implements ApiLinkSpecification {

        @Override
        public ApiLink getLink() {
            return ApiLink.of(UserController.class, c -> c.resetPassword(null));
        }
    }

}
