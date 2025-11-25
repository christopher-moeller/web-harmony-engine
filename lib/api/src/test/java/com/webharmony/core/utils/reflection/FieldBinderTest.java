package com.webharmony.core.utils.reflection;

import com.webharmony.core.AbstractBaseTest;
import com.webharmony.core.api.rest.model.UserAccessDto;
import com.webharmony.core.api.rest.model.UserDto;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class FieldBinderTest extends AbstractBaseTest {

    @Test
    void shouldBindFields() {

        final FieldBinder<UserDto, UserAccessDto> fieldBinder = FieldBinder.of(UserDto.class, new UserAccessDto())
                .withGetter(UserDto::getUserAccessConfig)
                .withSetter(UserDto::setUserAccessConfig).build();

        UserDto userDto = new UserDto();
        UserAccessDto userAccessDto = new UserAccessDto();

        fieldBinder.setValue(userDto, userAccessDto);

        assertThat(userDto.getUserAccessConfig())
                .isNotNull()
                .isEqualTo(userAccessDto);

        assertThat(fieldBinder.getValue(userDto))
                .isNotNull()
                .isEqualTo(userAccessDto);
    }

}
