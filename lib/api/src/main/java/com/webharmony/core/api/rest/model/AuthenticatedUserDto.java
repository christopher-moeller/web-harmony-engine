package com.webharmony.core.api.rest.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class AuthenticatedUserDto {

    private String username;
    private String firstname;
    private String lastname;
    private List<String> effectiveRights;

}
