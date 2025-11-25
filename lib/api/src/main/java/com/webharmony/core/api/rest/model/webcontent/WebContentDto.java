package com.webharmony.core.api.rest.model.webcontent;

import com.webharmony.core.api.rest.model.utils.AbstractResourceDto;
import com.webharmony.core.api.rest.model.utils.annotations.SelectableOptions;
import com.webharmony.core.api.rest.model.utils.apiobject.ApiResource;
import com.webharmony.core.api.rest.model.utils.optioncontainers.SelectableAreasForWebContentAreaOptionContainer;
import com.webharmony.core.api.rest.validation.WebContentDtoValidator;
import com.webharmony.core.service.data.validation.utils.Validation;
import com.webharmony.core.service.webcontent.model.AbstractWebContent;
import com.webharmony.core.service.webcontent.model.BoxAlignment;
import com.webharmony.core.service.webcontent.model.BoxWebContent;
import com.webharmony.core.utils.objects.ObjectsWithLabel;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
@Validation(WebContentDtoValidator.class)
public class WebContentDto extends AbstractResourceDto implements ObjectsWithLabel<String> {

    private String label;
    private AbstractWebContent content;

    @SelectableOptions(SelectableAreasForWebContentAreaOptionContainer.class)
    private ApiResource<?> area;

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    public static WebContentDto createInitialTemplate() {
        WebContentDto dto = new WebContentDto();

        final BoxWebContent root = new BoxWebContent();
        root.setAlign(BoxAlignment.VERTICAL);
        root.setChildren(new ArrayList<>());

        dto.setContent(root);

        return dto;
    }


}
