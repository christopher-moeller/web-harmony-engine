package com.webharmony.core.utils.dev.fepages;

import com.webharmony.core.AbstractBaseTest;

import com.webharmony.core.data.enums.ECoreActorRight;
import com.webharmony.core.utils.dev.fepages.json.*;
import com.webharmony.core.utils.dev.fepages.model.*;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


class PageProviderTest extends AbstractBaseTest {

    private static final String GENERIC_NAVIGATION_COMPONENT = "/components/view/GenericNavigationPage.vue";

    @Test
    void shouldGeneratePages() {
        final PageProvider pageProvider = createTestPageProvider();

        final RouterPagesRoot json = pageProvider.createJson();
        final List<PageJson> pages = json.getPages();

        assertThat(pages).hasSize(6);
        assertThat(pages.stream().map(PageJson::getPath))
                .contains("/users", "/users/userRights", "/users/userRights/[id]", "/users/userRoles", "/admin", "/landingPage");

        assertThat(pages.stream().map(PageJson::getId))
                .contains("users", "userRights", "userRightsById", "userRoles", "admin", "landingPage");

        assertThat(pages.stream().map(PageJson::getEnglishLabel))
                .contains("Users", "Rights", "Right", "Roles", "Administration", "Landing Page");

        assertThat(findPage(pages, "users").getFrontendComponent())
                .matches(FrontendComponentJson::getIsCoreComponent)
                .matches(c -> c.getComponentPath().equals(GENERIC_NAVIGATION_COMPONENT));

        assertThat(findPage(pages, "userRights").getFrontendComponent())
                .matches(c -> c.getIsCoreComponent() == null)
                .matches(c -> c.getComponentPath() == null);

        assertThat(findPage(pages, "userRightsById").getFrontendComponent())
                .matches(c -> c.getIsCoreComponent() == null)
                .matches(c -> c.getComponentPath() == null);

        assertThat(findPage(pages, "userRoles").getFrontendComponent())
                .matches(c -> c.getIsCoreComponent() == null)
                .matches(c -> c.getComponentPath() == null);

        assertThat(findPage(pages, "admin").getFrontendComponent())
                .matches(FrontendComponentJson::getIsCoreComponent)
                .matches(c -> c.getComponentPath().equals("/components/admin.vue"));

        assertThat(findPage(pages, "landingPage").getFrontendComponent())
                .matches(c -> !c.getIsCoreComponent())
                .matches(c -> c.getComponentPath().equals("/components/project-landing.vue"));


        final PageJson usersPage = pages.stream().filter(p -> p.getId().equals("users")).findAny().orElseThrow();
        assertThat(usersPage.getAccessRuleConfigJson())
                .isNotNull()
                .matches(u -> !u.getIsPublic())
                .matches(u -> !u.getIsAndConnected())
                .matches(u -> u.getRights().size() == 2)
                .matches(u -> u.getRights().contains(ECoreActorRight.CORE_ACTOR_RIGHTS_CRUD.name()) && u.getRights().contains(ECoreActorRight.CORE_USER_ROLES_CRUD.name()));

        final PageJson userRightsPage = pages.stream().filter(p -> p.getId().equals("userRights")).findAny().orElseThrow();
        assertThat(userRightsPage.getAccessRuleConfigJson())
                .isNotNull()
                .matches(u -> !u.getIsPublic())
                .matches(u -> !u.getIsAndConnected())
                .matches(u -> u.getRights().size() == 1)
                .matches(u -> u.getRights().contains(ECoreActorRight.CORE_ACTOR_RIGHTS_CRUD.name()));

        final PageJson userRightsIdPage = pages.stream().filter(p -> p.getId().equals("userRightsById")).findAny().orElseThrow();
        assertThat(userRightsIdPage.getAccessRuleConfigJson())
                .isNotNull()
                .matches(u -> !u.getIsPublic())
                .matches(u -> !u.getIsAndConnected())
                .matches(u -> u.getRights().size() == 1)
                .matches(u -> u.getRights().contains(ECoreActorRight.CORE_ACTOR_RIGHTS_CRUD.name()));

        final PageJson userRolesPage = pages.stream().filter(p -> p.getId().equals("userRoles")).findAny().orElseThrow();
        assertThat(userRolesPage.getAccessRuleConfigJson())
                .isNotNull()
                .matches(u -> !u.getIsPublic())
                .matches(u -> !u.getIsAndConnected())
                .matches(u -> u.getRights().size() == 1)
                .matches(u -> u.getRights().contains(ECoreActorRight.CORE_USER_ROLES_CRUD.name()));

        final PageJson adminPage = pages.stream().filter(p -> p.getId().equals("admin")).findAny().orElseThrow();
        assertThat(adminPage.getAccessRuleConfigJson())
                .isNotNull()
                .matches(u -> !u.getIsPublic())
                .matches(u -> !u.getIsAndConnected())
                .matches(u -> u.getRights().isEmpty());

        final PageJson landingPage = pages.stream().filter(p -> p.getId().equals("landingPage")).findAny().orElseThrow();
        assertThat(landingPage.getAccessRuleConfigJson())
                .isNotNull()
                .matches(AccessRuleConfigJson::getIsPublic)
                .matches(AccessRuleConfigJson::getIsAndConnected)
                .matches(u -> u.getRights().isEmpty());

    }

    @Test
    void shouldGenerateNavigationItems() {
        final PageProvider pageProvider = createTestPageProvider();

        final RouterPagesRoot json = pageProvider.createJson();

        final NavigationTreeJson navigationTree = json.getNavigationTree();
        assertThat(navigationTree.getItems())
                .hasSize(2);

        final NavigationItemJson usersItem = findNavigationItem(navigationTree.getItems(), "users");
        assertThat(usersItem.getIcon()).isEqualTo("users-icon");
        assertThat(usersItem.getChildren()).hasSize(2);

        final NavigationItemJson userRightsItems = findNavigationItem(usersItem.getChildren(), "userRights");
        assertThat(userRightsItems.getIcon()).isEqualTo("user-rights-icon");
        assertThat(userRightsItems.getChildren()).hasSize(1);

        final NavigationItemJson userRightsByIdItem = findNavigationItem(userRightsItems.getChildren(), "userRightsById");
        assertThat(userRightsByIdItem.getIcon()).isEqualTo("user-rights-id-icon");
        assertThat(userRightsByIdItem.getChildren()).isEmpty();

        final NavigationItemJson userRoles = findNavigationItem(usersItem.getChildren(), "userRoles");
        assertThat(userRoles.getIcon()).isEqualTo("user-roles-icon");
        assertThat(userRoles.getChildren()).isEmpty();

        final NavigationItemJson adminItem = findNavigationItem(navigationTree.getItems(), "admin");
        assertThat(adminItem.getIcon()).isEqualTo("admin-icon");
        assertThat(adminItem.getChildren()).isEmpty();
    }

    private PageProvider createTestPageProvider() {
        return new PageProvider() {
            @Override
            protected List<Page> createPages() {
                return List.of(
                        Page.of("users", "users", "Users", FrontendComponent.genericNavigationComponent(), PageAccessRule.collectFromChildren())
                                .withSubPages(
                                        Page.of("userRights", "userRights", "Rights", FrontendComponent.definedInFrontend(), PageAccessRule.requiresRight(ECoreActorRight.CORE_ACTOR_RIGHTS_CRUD))
                                                .withSubPages(
                                                        Page.of("userRightsById", "[id]", "Right", FrontendComponent.definedInFrontend(), PageAccessRule.requiresRight(ECoreActorRight.CORE_ACTOR_RIGHTS_CRUD))
                                                ),
                                        Page.of("userRoles", "userRoles", "Roles", FrontendComponent.definedInFrontend(), PageAccessRule.requiresRight(ECoreActorRight.CORE_USER_ROLES_CRUD))
                                ),
                        Page.of("admin", "admin", "Administration", FrontendComponent.ofCorePath("/components/admin.vue"), PageAccessRule.collectFromChildren()),
                        Page.of("landingPage", "landingPage", "Landing Page", FrontendComponent.ofProjectPath("/components/project-landing.vue"), PageAccessRule.publicAccess())
                );
            }

            @Override
            protected NavigationTree createNavigationTree() {
                return NavigationTree.ofItems(
                        NavigationItem.toPage(getPageById("users"), "users-icon").withChildren(
                                NavigationItem.toPage(getPageById("userRights"), "user-rights-icon").withChildren(
                                        NavigationItem.toPage(getPageById("userRightsById"), "user-rights-id-icon")
                                ),
                                NavigationItem.toPage(getPageById("userRoles"), "user-roles-icon")
                        ),
                        NavigationItem.toPage(getPageById("admin"), "admin-icon")
                );
            }
        };
    }

    private NavigationItemJson findNavigationItem(List<NavigationItemJson> items, String pageId) {
        return items.stream()
                .filter(p -> p.getPageId().equals(pageId))
                .findAny()
                .orElseThrow();
    }

    private PageJson findPage(List<PageJson> pages, String pageId) {
        return pages.stream()
                .filter(p -> p.getId().equals(pageId))
                .findAny()
                .orElseThrow();
    }

}