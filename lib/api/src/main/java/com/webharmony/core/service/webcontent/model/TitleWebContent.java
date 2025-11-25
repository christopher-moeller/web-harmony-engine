package com.webharmony.core.service.webcontent.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TitleWebContent extends AbstractWebContent {

    private String title;

    @Override
    public String getType() {
        return "title";
    }

}
