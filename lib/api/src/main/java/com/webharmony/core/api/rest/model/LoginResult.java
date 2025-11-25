package com.webharmony.core.api.rest.model;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResult {

    private String type;
    private String token;
    private String tokenHeaderField;

}
