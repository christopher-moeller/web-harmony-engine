package com.webharmony.core.api.rest.model.view.user;

import com.webharmony.core.api.rest.controller.user.UserController;
import com.webharmony.core.api.rest.model.utils.ViewModel;
import com.webharmony.core.api.rest.model.utils.annotations.ViewModelLinks;
import com.webharmony.core.api.rest.model.utils.annotations.utils.ApiLinkSpecification;
import com.webharmony.core.api.rest.validation.UserPersonalAccountChangePasswordVMValidation;
import com.webharmony.core.service.data.validation.utils.Validation;
import com.webharmony.core.utils.reflection.ApiLink;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Validation(UserPersonalAccountChangePasswordVMValidation.class)
@ViewModelLinks(
        saveLink = UserPersonalAccountChangePasswordVM.SaveLink.class
)
public class UserPersonalAccountChangePasswordVM implements ViewModel {

    private String oldPassword;
    private String newPassword;
    private String newPasswordAgain;

    public static final class SaveLink implements ApiLinkSpecification {

        @Override
        public ApiLink getLink() {
            return ApiLink.of(UserController.class, c -> c.changeUserPassword(null));
        }
    }

}
