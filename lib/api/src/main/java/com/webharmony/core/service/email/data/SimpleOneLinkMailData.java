package com.webharmony.core.service.email.data;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SimpleOneLinkMailData extends BaseEmailData {

    private String caption;
    private String message;
    private String btnText;
    private String btnLink;

}
