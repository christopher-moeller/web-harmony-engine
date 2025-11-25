package com.webharmony.core.service.email.data;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class BaseEmailData {

    private String emailToAddress;
    private String emailSubject;

}
