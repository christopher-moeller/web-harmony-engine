package com.webharmony.core.service.userregistration;

import lombok.Getter;

@Getter
public enum EUserRegistrationWorkflow {

    INVITATION_REGISTRATION_EMAIL(RegistrationConfiguration.builder()
            .isInvitationAllowed(true)
            .isRegistrationByInvitationAllowed(true)
            .isRegistrationFromScratchAllowed(false)
            .isEmailConfirmationAllowed(true)
            .isAdminConfirmationRequired(false)
            .build()),

    REGISTRATION_EMAIL(RegistrationConfiguration.builder()
            .isInvitationAllowed(true)
            .isRegistrationByInvitationAllowed(true)
            .isRegistrationFromScratchAllowed(true)
            .isEmailConfirmationAllowed(true)
            .isAdminConfirmationRequired(false)
            .build()),

    INVITATION_REGISTRATION_EMAIL_CONFIRMATION(RegistrationConfiguration.builder()
            .isInvitationAllowed(true)
            .isRegistrationByInvitationAllowed(true)
            .isRegistrationFromScratchAllowed(false)
            .isEmailConfirmationAllowed(true)
            .isAdminConfirmationRequired(true)
            .build()),

    REGISTRATION_EMAIL_CONFIRMATION(RegistrationConfiguration.builder()
            .isInvitationAllowed(true)
            .isRegistrationByInvitationAllowed(true)
            .isRegistrationFromScratchAllowed(true)
            .isEmailConfirmationAllowed(true)
            .isAdminConfirmationRequired(true)
            .build()),
    NO_REGISTRATION_ALLOWED(RegistrationConfiguration.builder()
            .isInvitationAllowed(false)
            .isRegistrationByInvitationAllowed(false)
            .isRegistrationFromScratchAllowed(false)
            .isEmailConfirmationAllowed(false)
            .isAdminConfirmationRequired(false)
            .build());

    private final RegistrationConfiguration registrationConfiguration;

    EUserRegistrationWorkflow(RegistrationConfiguration registrationConfiguration) {
        this.registrationConfiguration = registrationConfiguration;
    }
}
