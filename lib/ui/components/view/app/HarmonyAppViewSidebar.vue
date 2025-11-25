<template>
  <div class="sidebar" :class="isOpen ? '' : 'close'">
    <HarmonyViewLogo
        :is-in-app-mode="true"
        :name="name"
    />
    <ul class="side-menu">
      <li v-for="(item, index) in itemData" :class="getLiClass(index)">
        <NuxtLink :to="item.link"><i class="bx" :class="item.icon"></i>{{ getTranslatedLabel(item.englishLabel) }}</NuxtLink>
      </li>
    </ul>
    <ul class="side-menu">
      <li>
        <a class="logout" @click="onLogoutClick">
          <i class='bx bx-log-out-circle'></i>
          {{ i18n.translate('Logout').build() }}
        </a>
      </li>
    </ul>
    <div id="report-bug-area">
      <ul class="side-menu">
        <li>
          <i id="report-bug-icon" class="bx bx-bug-alt" @click="onReportBugClick"></i>
        </li>
      </ul>
    </div>
  </div>
  <HarmonyModal v-model="showBugReportModal">
    <ReportBugWindow @onBugReported="onBugReported"/>
  </HarmonyModal>
</template>

<script lang="ts" setup>
import {ref} from 'vue';
import I18N from "~/utils/I18N";
import HarmonyModal from "~/components/base/HarmonyModal.vue";
import ReportBugWindow from "~/components/utils/ReportBugWindow.vue";
import useAuthenticationContext from "~/composables/useAuthenticationContext";
import useNavigationTree from "~/composables/useNavigationTree";
import {ECoreRouterPage, NavigationItemJson} from "~/CoreApi";
import HarmonyViewLogo from "~/components/view/HarmonyViewLogo.vue";
import useRouterUtils from "~/composables/useRouterUtils";
import useProjectMetaData from "~/composables/useProjectMetaData";

const props = defineProps({
  isOpen: {
    type: Boolean,
    required: true,
    default: false
  }
})

const i18n = I18N.of("HarmonyAuthenticatedSidebar")

const routerUtils = useRouterUtils()

const navigationTree = useNavigationTree()

const showBugReportModal = ref(false)

const authenticationContext = useAuthenticationContext()

const getFilteredItems = async () => {
  const result:NavigationItemJson[] = []
  const rootItems = navigationTree.getTree().items!
  for (let rootItem of rootItems) {
    const hasAccess = await authenticationContext.isAuthorizedByAccessRuleConfig(rootItem.accessRule!)
    if(hasAccess) {
      result.push(rootItem)
    }
  }
  return result
}

const itemData:NavigationItemJson[] = await getFilteredItems()

const activeItemIndex = computed(() => {
  const routeName = <string> routerUtils.getCurrentRoute().value.name
  const path = navigationTree.findTreeItemPathByPageId(routeName)
  if(path) {
    const rootElement = path[0].pageId
    return itemData.findIndex(item => item.pageId === rootElement)
  } else {
    return -1;
  }
})

const getLiClass = (index: number) => index === activeItemIndex.value ? 'active' : ''

const getTranslatedLabel = (englishLabel: string) => I18N.staticTranslateRouterPagesJsonLabel(englishLabel).build()

const onLogoutClick = async () => {
  await useAuthenticationContext().logoutAndClear()
  await routerUtils.hardNavigateToPage(ECoreRouterPage.INDEX)
}

const onReportBugClick = () => {
  showBugReportModal.value = true
}

const onBugReported = () => showBugReportModal.value = false

const projectMeta = useProjectMetaData()
const name = projectMeta.projectShortName

</script>

<style>
.sidebar {
  position: fixed;
  top: 0;
  left: 0;
  background: var(--harmony-light-1);
  width: 230px;
  height: 100%;
  z-index: 2000;
  overflow-x: hidden;
  scrollbar-width: none;
  transition: all 0.3s ease;
}

.sidebar.close ~ .content {
  width: calc(100% - 60px);
  left: 60px;
}

.sidebar::-webkit-scrollbar {
  display: none;
}

.sidebar.close {
  width: 60px;
}

.sidebar .side-menu {
  width: 100%;
  margin-top: 48px;
}

.sidebar .side-menu li {
  height: 48px;
  background: transparent;
  margin-left: 6px;
  border-radius: 48px 0 0 48px;
  padding: 4px;
}

.sidebar .side-menu li.active {
  background: var(--harmony-light-2);
  position: relative;
}

.sidebar .side-menu li.active::before {
  content: "";
  position: absolute;
  width: 40px;
  height: 40px;
  border-radius: 50%;
  top: -40px;
  right: 0;
  box-shadow: 20px 20px 0 var(--harmony-light-2);
  z-index: -1;
}

.sidebar .side-menu li.active::after {
  content: "";
  position: absolute;
  width: 40px;
  height: 40px;
  border-radius: 50%;
  bottom: -40px;
  right: 0;
  box-shadow: 20px -20px 0 var(--harmony-light-2);
  z-index: -1;
}

.sidebar .side-menu li a {
  width: 100%;
  height: 100%;
  background: var(--harmony-light-1);
  display: flex;
  align-items: center;
  border-radius: 48px;
  font-size: 16px;
  color: var(--harmony-black);
  white-space: nowrap;
  overflow-x: hidden;
  transition: all 0.3s ease;
}

.sidebar .side-menu li.active a {
  color: var(--harmony-success);
}

.sidebar.close .side-menu li a {
  width: calc(48px - (4px * 2));
  transition: all 0.3s ease;
}

.sidebar .side-menu li a .bx {
  min-width: calc(60px - ((4px + 6px) * 2));
  display: flex;
  font-size: 1.6rem;
  justify-content: center;
}

.sidebar .side-menu li a.logout {
  color: var(--harmony-danger);
  cursor: pointer;
}

.sidebar.close ~ .content {
  width: calc(100% - 60px);
  left: 60px;
}

@media screen and (max-width: 768px) {
  .sidebar {
    width: 200px;
  }
}

#report-bug-area {
  display: flex;
  position: absolute;
  bottom: 0;
  left: 6px;
}

#report-bug-icon {
  color: var(--harmony-light-3);
  cursor: pointer;
}
</style>