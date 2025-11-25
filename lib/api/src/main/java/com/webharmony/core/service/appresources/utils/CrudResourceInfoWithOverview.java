package com.webharmony.core.service.appresources.utils;

import com.webharmony.core.api.rest.model.utils.ResourceOverviewTypeSchema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CrudResourceInfoWithOverview extends CrudResourceInfoDto {

    private ResourceOverviewTypeSchema resourceOverviewTypeSchema;

    public static CrudResourceInfoWithOverview of(CrudResourceInfoDto infoDto, ResourceOverviewTypeSchema overviewTypeSchema) {
        CrudResourceInfoWithOverview overviewDto = new CrudResourceInfoWithOverview();
        overviewDto.setResourceOverviewTypeSchema(overviewTypeSchema);

        overviewDto.setComplexTypeSchema(infoDto.getComplexTypeSchema());
        overviewDto.setGetAllLink(infoDto.getGetAllLink());
        overviewDto.setGetByIdLink(infoDto.getGetByIdLink());
        overviewDto.setCreateNewLink(infoDto.getCreateNewLink());
        overviewDto.setUpdateLink(infoDto.getUpdateLink());
        overviewDto.setDeleteLink(infoDto.getDeleteLink());
        overviewDto.setGetTemplateLink(infoDto.getGetTemplateLink());

        overviewDto.setName(infoDto.getName());
        overviewDto.setPageableOptions(infoDto.getPageableOptions());

        return overviewDto;
    }
}
