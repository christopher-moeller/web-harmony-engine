package com.webharmony.core.api.rest.model.view.user;

import com.webharmony.core.api.rest.controller.user.UserController;
import com.webharmony.core.api.rest.model.utils.ViewModel;
import com.webharmony.core.api.rest.model.utils.annotations.ReadOnlyAttribute;
import com.webharmony.core.api.rest.model.utils.annotations.ViewModelLinks;
import com.webharmony.core.api.rest.model.utils.annotations.utils.ApiLinkSpecification;
import com.webharmony.core.api.rest.validation.UserPersonalAccountResetPasswordByEmailVMValidation;
import com.webharmony.core.service.data.validation.utils.Validation;
import com.webharmony.core.utils.reflection.ApiLink;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Validation(UserPersonalAccountResetPasswordByEmailVMValidation.class)
@ViewModelLinks(
        saveLink = UserPersonalAccountResetPasswordByEmailVM.SaveLink.class,
        loadLink = UserPersonalAccountResetPasswordByEmailVM.LoadLink.class
)
public class UserPersonalAccountResetPasswordByEmailVM implements ViewModel {

    @ReadOnlyAttribute
    private String email;
    @ReadOnlyAttribute
    private String tokenValue;
    private String password;
    private String passwordAgain;

    public static final class SaveLink implements ApiLinkSpecification {

        @Override
        public ApiLink getLink() {
            return ApiLink.of(UserController.class, c -> c.resetPasswordByEmailVM(null));
        }
    }

    public static final class LoadLink implements ApiLinkSpecification {

        @Override
        public ApiLink getLink() {
            return ApiLink.of(UserController.class, c -> c.getViewModelForResetPasswordByEmail(null));
        }
    }
}
