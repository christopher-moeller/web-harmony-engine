package com.webharmony.core.service.webcontent.model;

import com.webharmony.core.api.rest.model.utils.FileWebData;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ImageWebContent extends AbstractWebContent {

    private String imageTitle;
    private FileWebData fileWebData;
    private Integer width;
    private Integer height;
    private Boolean showImageTitle;

    @Override
    public String getType() {
        return "image";
    }

}
