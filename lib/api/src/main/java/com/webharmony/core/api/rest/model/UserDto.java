package com.webharmony.core.api.rest.model;


import com.webharmony.core.api.rest.model.utils.AbstractResourceDto;
import com.webharmony.core.api.rest.validation.UserDtoValidation;
import com.webharmony.core.service.data.validation.utils.Validation;
import com.webharmony.core.utils.objects.ObjectsWithLabel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Validation(UserDtoValidation.class)
public class UserDto extends AbstractResourceDto implements ObjectsWithLabel<String> {

    private String label;
    private String email;
    private String firstname;
    private String lastname;
    private UserAccessDto userAccessConfig;

    public static UserDto createInitialTemplate() {
        UserDto userDto = new UserDto();

        UserAccessDto userAccessDto = new UserAccessDto();
        userAccessDto.setIsAdmin(false);
        userDto.setUserAccessConfig(userAccessDto);

        return userDto;
    }

}
