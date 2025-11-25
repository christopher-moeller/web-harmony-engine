package com.webharmony.core.api.rest.model.view.user;

import com.webharmony.core.api.rest.controller.user.UserRegistrationController;
import com.webharmony.core.api.rest.model.utils.ViewModel;
import com.webharmony.core.api.rest.model.utils.annotations.ViewModelLinks;
import com.webharmony.core.api.rest.model.utils.annotations.utils.ApiLinkSpecification;
import com.webharmony.core.api.rest.validation.UserRegistrationVMValidation;
import com.webharmony.core.service.data.validation.utils.Validation;
import com.webharmony.core.utils.reflection.ApiLink;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Validation(UserRegistrationVMValidation.class)
@ViewModelLinks(
        saveLink = UserRegistrationVM.SaveLink.class
)
public class UserRegistrationVM implements ViewModel {

    private String email;
    private String firstname;
    private String lastname;
    private String password;
    private String passwordAgain;

    public static final class SaveLink implements ApiLinkSpecification {

        @Override
        public ApiLink getLink() {
            return ApiLink.of(UserRegistrationController.class, c -> c.createNewRegistrationFromScratch(null));
        }
    }




}
