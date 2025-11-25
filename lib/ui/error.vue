<template>
  <client-only>
    <harmony-panel :caption="i18n.translate('Error').build()" class="panel">
      <p>{{i18n.translate('Unfortunately something went wrong').build()}}</p>
      <div v-if="userCanReadDetailMessage && showDetails" style="margin-top: 1em;">
        <p><b>Details:</b></p>
        <pre>{{ error }}</pre>
        <div v-if="error.stack">
          <p><b>Stack:</b></p>
          <div v-html="error.stack"/>
        </div>
      </div>
      <div class="btn-row">
        <harmony-button v-if="redirectFrom" :caption="i18n.translate('Back').build()" class="back-btn" @click="onBackClick" />
        <harmony-button v-else :caption="i18n.translate('Home').build()" class="back-btn" @click="onHomeClick" />
        <harmony-button v-if="userCanReadDetailMessage" :type="HarmonyButtonType.SECONDARY" :caption="i18n.translate('Details').build()" class="back-btn" @click="onShowDetailsClick" />
      </div>
    </harmony-panel>
  </client-only>
  </template>
  
  <script lang="ts" setup>

  import HarmonyPanel from "@core/components/base/HarmonyPanel.vue";
  import HarmonyButton from "@core/components/base/HarmonyButton.vue";
  import I18N from "@core/utils/I18N";
  import {HarmonyButtonType} from "~/utils/HarmonyTypes";
  import useAuthenticationContext from "~/composables/useAuthenticationContext";
  import useRouterUtils from "~/composables/useRouterUtils";
  import {ECoreRouterPage} from "~/CoreApi";

  const i18n = I18N.of("error")

  const routerUtils = useRouterUtils()
  const redirectFrom = routerUtils.getCurrentRoute().value.query.redirectFrom

  const authenticationContext = useAuthenticationContext()
  const isAuthenticated = await authenticationContext.getIsAuthenticated()
  const userCanReadDetailMessage = ref(true)//isAuthenticated && await authenticationContext.hasRight(ECoreActorRight.CORE_ERROR_MESSAGE_DETAIL_READ)

  const onBackClick = () => {
    routerUtils.softNavigateToPath(redirectFrom+"")
  }
  
  const onHomeClick = () => {
    routerUtils.softNavigateToPage(ECoreRouterPage.INDEX)
  }

  const error = useError();

  const showDetails = ref(false)

  const onShowDetailsClick = () => showDetails.value = !showDetails.value
  
  </script>
  
  <style scoped>

  .panel {
    margin-top: 5%;
    margin-left: 5%;
    margin-right: 5%;
  }
  
  .btn-row {
    display: flex;
    align-items: center;
    justify-content: center;
  }
  
  .back-btn {
    margin-top: 30px;
    height: 40px;
    width: 10rem;
    margin-left: 5px;
  }
  
  </style>