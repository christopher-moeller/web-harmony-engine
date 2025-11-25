package com.webharmony.core.data.jpa.model.userregistration;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AppUserRegistrationStateData {

    private String invitationSendAt;
    private String email;
    private String firstname;
    private String lastname;
    private String encryptedPassword;
    private String emailConfirmedAt;
    private String adminConfirmedAt;
    private String userCreatedAt;


}
