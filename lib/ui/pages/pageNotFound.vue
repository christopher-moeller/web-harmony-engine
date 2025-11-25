<template>
  <harmony-panel :caption="i18n.translate('Page not found').build()" class="panel">
    <p>{{i18n.translate('Unfortunately this page does not exists').build()}}</p>
    <div class="btn-row">
      <harmony-button v-if="redirectFrom" :caption="i18n.translate('Back').build()" class="back-btn" @click="onBackClick" />
      <harmony-button v-else :caption="i18n.translate('Home').build()" class="back-btn" @click="onHomeClick" />
    </div>
  </harmony-panel>
</template>

<script lang="ts" setup>

import HarmonyPanel from "~/components/base/HarmonyPanel.vue";
import HarmonyButton from "~/components/base/HarmonyButton.vue";
import I18N from "~/utils/I18N";
import useRouterUtils from "~/composables/useRouterUtils";
import {ECoreRouterPage} from "~/CoreApi";

const i18n = I18N.of("pageNotFound")

const routerUtils = useRouterUtils()
const redirectFrom = routerUtils.getCurrentRoute().value.query.redirectFrom

const onBackClick = () => {
  routerUtils.softNavigateToPath(redirectFrom+"")
}

const onHomeClick = () => {
  routerUtils.softNavigateToPage(ECoreRouterPage.INDEX)
}

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
}

</style>