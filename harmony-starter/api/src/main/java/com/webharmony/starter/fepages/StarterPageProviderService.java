package com.webharmony.starter.fepages;

import com.webharmony.core.utils.dev.fepages.PageProvider;
import com.webharmony.core.utils.dev.fepages.PageProviderService;
import com.webharmony.core.utils.dev.fepages.model.FrontendComponent;
import com.webharmony.core.utils.dev.fepages.model.NavigationTree;
import com.webharmony.core.utils.dev.fepages.model.Page;
import com.webharmony.core.utils.dev.fepages.model.PageAccessRule;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Primary
public class StarterPageProviderService extends PageProviderService {

    @Override
    protected PageProvider initPageProvider() {
        return new PageProvider() {
            @Override
            protected List<Page> createPages() {
                return List.of(
                        Page.of("index", "", "Index", FrontendComponent.definedInFrontend(), PageAccessRule.publicAccess()),
                        Page.of("forbidden", "forbidden", "Forbidden", FrontendComponent.definedInFrontend(), PageAccessRule.publicAccess()),
                        Page.of("pageNotFound", "pageNotFound", "Page not found", FrontendComponent.definedInFrontend(), PageAccessRule.publicAccess()),
                        Page.of("language", "language", "Language", FrontendComponent.definedInFrontend(), PageAccessRule.publicAccess())
                );
            }

            @Override
            protected NavigationTree createNavigationTree() {
                return NavigationTree.ofItems();
            }
        };
    }
}
