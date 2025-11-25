package com.webharmony.core.service.webcontent.model;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class BoxWebContent extends AbstractWebContentWithChildren {

    private List<AbstractWebContent> children = new ArrayList<>();

    private BoxAlignment align = BoxAlignment.VERTICAL;

    public void addChild(AbstractWebContent child) {
        children.add(child);
    }

    @Override
    public String getType() {
        return "box";
    }

}
