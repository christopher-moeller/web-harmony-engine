package com.webharmony.core.service.userregistration;

import com.webharmony.core.api.rest.model.utils.BaseDto;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class RegistrationConfiguration extends BaseDto {

    private boolean isInvitationAllowed;
    private boolean isRegistrationByInvitationAllowed;
    private boolean isRegistrationFromScratchAllowed;
    private boolean isEmailConfirmationAllowed;
    private boolean isAdminConfirmationRequired;

}
