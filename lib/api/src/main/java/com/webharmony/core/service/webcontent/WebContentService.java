package com.webharmony.core.service.webcontent;

import com.webharmony.core.service.webcontent.model.*;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@Service
public class WebContentService {

    public AbstractWebContent getExampleContent() {
        final BoxWebContent root = new BoxWebContent();
        root.addChild(createTitleContent("Root Title"));
        root.addChild(createExample1());
        root.addChild(createExample2());
        return root;
    }

    private AbstractWebContent createExample1() {
        final BoxWebContent root = new BoxWebContent();
        root.setAlign(BoxAlignment.HORIZONTAL);
        root.addChild(createTitleContent("Example 1"));
        root.addChild(createTextContent("Text 1"));
        return root;
    }

    private AbstractWebContent createExample2() {
        final BoxWebContent root = new BoxWebContent();
        root.setAlign(BoxAlignment.HORIZONTAL);
        root.addChild(createTextContent("A"));
        root.addChild(createTextContent("B"));
        root.addChild(createTextContent("C"));
        return root;
    }

    private TitleWebContent createTitleContent(String title) {
        TitleWebContent titleWebContent = new TitleWebContent();
        titleWebContent.setTitle(title);
        return titleWebContent;
    }

    private TextWebContent createTextContent(String text) {
        TextWebContent textWebContent = new TextWebContent();
        textWebContent.setText(text);
        return textWebContent;
    }

}
