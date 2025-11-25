package com.webharmony.core.api.rest.model.view.user;

import com.webharmony.core.api.rest.controller.user.UserController;
import com.webharmony.core.api.rest.model.utils.ViewModel;
import com.webharmony.core.api.rest.model.utils.annotations.ReadOnlyAttribute;
import com.webharmony.core.api.rest.model.utils.annotations.ViewModelLinks;
import com.webharmony.core.api.rest.model.utils.annotations.utils.ApiLinkSpecification;
import com.webharmony.core.api.rest.validation.UserPersonalAccountVMValidation;
import com.webharmony.core.service.data.validation.utils.Validation;
import com.webharmony.core.utils.reflection.ApiLink;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Validation(UserPersonalAccountVMValidation.class)
@ViewModelLinks(
        saveLink = UserPersonalAccountVM.SaveLink.class,
        loadLink = UserPersonalAccountVM.LoadLink.class
)
public class UserPersonalAccountVM implements ViewModel {

    @ReadOnlyAttribute
    private String email;
    private String firstname;
    private String lastname;

    public static final class SaveLink implements ApiLinkSpecification {

        @Override
        public ApiLink getLink() {
            return ApiLink.of(UserController.class, c -> c.savePersonalAccount(null));
        }
    }

    public static final class LoadLink implements ApiLinkSpecification {

        @Override
        public ApiLink getLink() {
            return ApiLink.of(UserController.class, UserController::getPersonalAccount);
        }
    }
}
