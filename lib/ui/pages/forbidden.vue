<template>
  <harmony-panel :caption="i18n.translate('Access Denied').build()" class="panel">
    <p>{{i18n.translate('Unfortunately you are not allowed to visit this page').build()}}</p>
    <div class="btn-row">
      <harmony-button :caption="i18n.translate('Home').build()" class="back-btn" @click="onBackClick" />
    </div>
  </harmony-panel>
</template>

<script lang="ts" setup>

import HarmonyPanel from "~/components/base/HarmonyPanel.vue";
import HarmonyButton from "~/components/base/HarmonyButton.vue";
import I18N from "~/utils/I18N";
import useRouterUtils from "~/composables/useRouterUtils";
import {ECoreRouterPage} from "~/CoreApi";

const i18n = I18N.of("forbidden")

const routerUtils = useRouterUtils()

const onBackClick = () => {
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