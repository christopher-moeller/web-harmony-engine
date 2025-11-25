package com.webharmony.core.api.rest.model.view.user;

import com.webharmony.core.api.rest.controller.AuthenticationController;
import com.webharmony.core.api.rest.model.utils.ViewModel;
import com.webharmony.core.api.rest.model.utils.annotations.ViewModelLinks;
import com.webharmony.core.api.rest.model.utils.annotations.utils.ApiLinkSpecification;
import com.webharmony.core.api.rest.validation.UserLoginVMValidation;
import com.webharmony.core.service.data.validation.utils.Validation;
import com.webharmony.core.utils.reflection.ApiLink;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Validation(UserLoginVMValidation.class)
@ViewModelLinks(
        saveLink = UserLoginVM.SaveLink.class
)
public class UserLoginVM implements ViewModel {

    private String username;
    private String password;

    public static final class SaveLink implements ApiLinkSpecification {

        @Override
        public ApiLink getLink() {
            return ApiLink.of(AuthenticationController.class, c -> c.userLogin(null, null));
        }
    }
}
