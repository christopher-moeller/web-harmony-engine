package com.webharmony.core.utils.dev.fepages;

import com.webharmony.core.utils.dev.fepages.json.RouterPagesRoot;
import org.springframework.stereotype.Service;

@Service
public class PageProviderService {

    public RouterPagesRoot buildRouterPages() {
        PageProvider pageProvider = initPageProvider();
        return pageProvider.createJson();
    }

    protected PageProvider initPageProvider() {
        return new CorePageProvider();
    }

}
