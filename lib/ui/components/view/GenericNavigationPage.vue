<template>
  <HarmonyAuthenticatedPageHeader>
    <template v-slot:subHeader>
      <GenericBreadcrumbNavigation />
    </template>
  </HarmonyAuthenticatedPageHeader>
  <ul>
    <li v-for="(subItem, index) in filteredSubItems" :key="index">
      <NuxtLink :to="subItem.link">
        <harmony-panel class="sub-item-panel">
          <i :class="getIconClassByItem(subItem)"></i>
          <p>{{getSubItemLabel(subItem)}}</p>
        </harmony-panel>
      </NuxtLink>
    </li>
  </ul>
</template>

<script lang="ts" setup>

import HarmonyPanel from "~/components/base/HarmonyPanel.vue";
import HarmonyAuthenticatedPageHeader from "~/components/view/app/HarmonyAppViewPageHeader.vue";
import useAuthenticationContext from "~/composables/useAuthenticationContext";
import useNavigationTree from "~/composables/useNavigationTree";
import {NavigationItemJson} from "~/CoreApi";
import GenericBreadcrumbNavigation from "~/components/utils/GenericBreadcrumbNavigation.vue";
import useRouterUtils from "~/composables/useRouterUtils";

const routerUtils = useRouterUtils()

const authenticationContext = useAuthenticationContext()
const navigationTree = useNavigationTree()
const pageId:string = <string> routerUtils.getCurrentRoute().value.name
const item = navigationTree.findTreeItemByPageId(pageId)

const getFilteredSubItems = async (): Promise<NavigationItemJson[]> => {
  const resultList:NavigationItemJson[] = []
  const items = item?.children ? item.children : []
  for (let subItem of items) {
    const hasAccess = await authenticationContext.isAuthorizedByAccessRuleConfig(subItem.accessRule)
    if(hasAccess) {
      resultList.push(subItem)
    }
  }

  return resultList;
}

const filteredSubItems = await getFilteredSubItems()

const getIconClassByItem = (item: NavigationItemJson) => {
  const itemIcon = item.icon ? item.icon : "bx-right-arrow-alt"
  return 'bx ' + itemIcon + ' bx-lg'
}

const getSubItemLabel = (item: NavigationItemJson) => {
  return I18N.staticTranslateRouterPagesJsonLabel(item.englishLabel+"").build()
}

</script>

<style scoped>

.sub-item-panel {
  cursor: pointer;
  color: var(--harmony-black);
  text-decoration: none;
  width: 15em;
  height: 150px;
}

ul {
  padding: 0;
  margin: 0;
  display: flex;
  flex-wrap: wrap;
}

li {
  padding: 5px;
  margin: 10px;
}


</style>