package com.webharmony.core.utils.dev.projectconfig;

import com.webharmony.core.utils.dev.fepages.json.RouterPagesRoot;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FrontendProjectConfiguration {

    private ProjectMetaData projectMeta;
    private RouterPagesRoot routerPages;

}
