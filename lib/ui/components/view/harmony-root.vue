<template>
  <ClientOnly>
    <PageLoadingSpinner v-if="inRoutingMode"/>
  </ClientOnly>
  <template v-if="isServerAvailable">
    <!-- switch here between app and web / decide this if router path starts with app or not (rules will be checked in router only)-->
    <template v-if="isAppPage">
      <HarmonyAppView />
    </template>
    <template v-else>
      <HarmonyWebView />
    </template>
  </template>
  <template v-else>
    <ServerNotReachable />
  </template>
  <client-only>
    <harmony-banner-controller />
  </client-only>
</template>

<script setup lang="ts">

import HarmonyAppView from "~/components/view/app/HarmonyAppView.vue";
import ServerNotReachable from "~/components/view/ServerNotReachable.vue";
import PageLoadingSpinner from "~/components/utils/PageLoadingSpinner.vue";
import {computed} from "vue";
import {useRouterStore} from "~/store/routerStore";

import HarmonyWebView from "~/components/view/web/HarmonyWebView.vue";
import useRouterUtils from "~/composables/useRouterUtils";
import HarmonyBannerController from "~/components/banner/HarmonyBannerController.vue";
import useCoreApi from "~/composables/useCoreApi";
import {useCookie} from "nuxt/app";
import {useI18nStore} from "~/store/i18nStore";
import {HarmonyRestError} from "~/utils/HarmonyTypes";
import {useNotificationStore} from "~/store/notificationStore";
import useHarmonyCookies from "~/composables/useHarmonyCookies";
import useAuthenticationContext from "~/composables/useAuthenticationContext";

async function checkIsServerAvailable(): Promise<boolean> {
  const coreApi = useCoreApi()
  try {
    const backendServerInfo = await coreApi.api().getServerInfoApi().baseApiInfo()
    return !!backendServerInfo?.data?.serverIsAvailable;
  }catch (e) {
    const restError:HarmonyRestError = <HarmonyRestError> e
    return !!restError.axiosResponse
  }
}

async function initI18nInternal(): Promise<void> {
  const coreApi = useCoreApi()
  const language = useCookie('language')
  if(!language.value) {
    const defaultLanguageResponse = await coreApi.api().getServerInfoApi().getDefaultLanguage()
    language.value = defaultLanguageResponse.data.id+""
  }

  const response = await coreApi.api().getI18nKeyEntryApi().getFrontendTranslation(language.value)
  useI18nStore().setFrontendTranslations(response.data)

  const languageInfoResponse = await coreApi.api().getServerInfoApi().getLanguageById(language.value)
  useI18nStore().setLanguageInfo(languageInfoResponse.data)
}


const isServerAvailable = await checkIsServerAvailable()
if(isServerAvailable) {
  await initI18nInternal()
}

const routerStore = useRouterStore()
const routerUtils = useRouterUtils()
const nuxtApp = useNuxtApp();

const isAppPage = computed(() => routerUtils.getCurrentRoute().value.path.startsWith("/app"))
const authenticationContext = useAuthenticationContext()

const notificationStore = useNotificationStore()
const harmonyCookies = useHarmonyCookies()

nuxtApp.hook("page:start", () => {
  routerStore.setIsInRoutingMode(true)
});
nuxtApp.hook("page:finish", () => {
  routerStore.setIsInRoutingMode(false)
  document.title = routerUtils.getTitleOfCurrentPage()
  authenticationContext.getIsAuthenticated().then(isAuthenticated => {
    if(isAuthenticated) {
      notificationStore.init(harmonyCookies)
    }
  })
});

const inRoutingMode = computed(() => routerStore.getIsInRoutingMode)

const coreApi = useCoreApi()

onMounted(() => {
  document.title = routerUtils.getTitleOfCurrentPage()
  window.onerror = function(msg, url, line, col, error) {
    coreApi.api().getApplicationExceptionApi().reportFrontendException({
      message: msg+"",
      url,
      line,
      col,
      error: error ? JSON.stringify(error) : undefined
    })
  }
})

</script>

<style scoped>

</style>