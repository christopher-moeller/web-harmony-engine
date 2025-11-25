package com.webharmony.core.utils.dev.fepages;

import com.webharmony.core.configuration.security.ApplicationRight;
import com.webharmony.core.utils.CollectionUtils;
import com.webharmony.core.utils.dev.fepages.json.*;
import com.webharmony.core.utils.dev.fepages.model.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public abstract class PageProvider {

    private List<Page> cachedPages;

    public RouterPagesRoot createJson() {

        final RouterPagesRoot root = new RouterPagesRoot();
        root.setPages(createPageJsons());
        root.setNavigationTree(createEffectiveNavigationTree());

        return root;
    }

    private List<PageJson> createPageJsons() {

        final List<PageJson> resultList = new ArrayList<>();
        getPages().forEach(page -> resultList.addAll(createEffectivePagesByPage(page)));

        return resultList;
    }

    private NavigationTreeJson createEffectiveNavigationTree() {
        NavigationTree rootItem = createNavigationTree();
        return mapToJson(rootItem);
    }

    private NavigationTreeJson mapToJson(NavigationTree navigationTree) {
        final NavigationTreeJson json = new NavigationTreeJson();
        final List<NavigationItemJson> items = navigationTree.getItems().stream().map(this::mapToJson).toList();
        json.setItems(items);
        return json;
    }

    private NavigationItemJson mapToJson(NavigationItem navigationItem) {
        final NavigationItemJson json  = new NavigationItemJson();
        json.setPageId(navigationItem.getPage().getId());
        json.setEnglishLabel(navigationItem.getPage().getEnglishLabel());
        json.setLink(navigationItem.getPage().getFullPath());
        json.setIcon(navigationItem.getIcon());
        json.setAccessRule(mapToJson(navigationItem.getPage().getAccessRuleConfig()));
        json.setChildren(navigationItem.getChildren().stream().map(this::mapToJson).toList());
        return json;
    }

    private List<PageJson> createEffectivePagesByPage(Page page) {
        final List<PageJson> resultList = new ArrayList<>();
        if (!page.getIsVirtualPage())
            resultList.add(mapToJson(page));
        page.getSubPages().forEach(subPage -> resultList.addAll(createEffectivePagesByPage(subPage)));
        return resultList;
    }

    private PageJson mapToJson(Page page) {
        final PageJson json = new PageJson();
        json.setId(page.getId());
        json.setIsCorePage(page.getIsCorePage());
        json.setPath(page.getFullPath());
        json.setEnglishLabel(page.getEnglishLabel());
        json.setFrontendComponent(mapToJson(page.getFrontendComponent()));
        json.setAccessRuleConfigJson(mapToJson(page.getAccessRuleConfig()));

        return json;
    }

    private FrontendComponentJson mapToJson(FrontendComponent frontendComponent) {
        FrontendComponentJson json = new FrontendComponentJson();
        json.setComponentPath(frontendComponent.getComponentPath());
        json.setIsCoreComponent(frontendComponent.getIsCoreComponent());
        return json;
    }

    private AccessRuleConfigJson mapToJson(AccessRuleConfig accessRuleConfig) {
        final AccessRuleConfigJson json = new AccessRuleConfigJson();
        json.setIsPublic(accessRuleConfig.getIsPublic());
        json.setIsAndConnected(accessRuleConfig.getIsAndConnected());
        json.setRights(accessRuleConfig.getRights().stream().map(ApplicationRight::name).toList());
        return json;
    }

    private List<Page> getPages() {
        if(cachedPages == null) {
            cachedPages = createPages();
        }
        return cachedPages;
    }

    protected abstract List<Page> createPages();

    protected abstract NavigationTree createNavigationTree();

    protected Page getPageById(String id) {

        for (Page page : getPages()) {
            Optional<Page> recursiveFinding = findRecursive(page, id);
            if(recursiveFinding.isPresent())
                return recursiveFinding.get();
        }

        throw new IllegalArgumentException(String.format("Page with id '%s' is not defined", id));
    }

    private Optional<Page> findRecursive(Page page, String id) {
        if(page.getId().equals(id)) {
            return Optional.of(page);
        } else {
            for (Page subPage : CollectionUtils.emptyListIfNull(page.getSubPages())) {
                final Optional<Page> recursiveFinding = findRecursive(subPage, id);
                if(recursiveFinding.isPresent()) {
                    return recursiveFinding;
                }
            }

            return Optional.empty();
        }
    }


}
