package com.webharmony.core.service.webcontent.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TextWebContent extends AbstractWebContent {

    private String text;

    @Override
    public String getType() {
        return "text";
    }
}
