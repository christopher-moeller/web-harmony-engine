package com.webharmony.core.api.rest.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ConfirmEmailByTokenBody {
    private String token;
}
