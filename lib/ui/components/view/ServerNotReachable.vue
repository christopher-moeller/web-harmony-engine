<template>
  <harmony-panel :caption="i18n.translate('Server not reachable').build()" class="panel">
    <p>{{i18n.translate('Unfortunately, the server is currently unavailable. Please try again later.').build()}}</p>
  </harmony-panel>
</template>

<script lang="ts" setup>

import I18N from "~/utils/I18N";
import HarmonyPanel from "~/components/base/HarmonyPanel.vue";
import {onMounted, onUnmounted} from "vue";
import useCoreApi from "~/composables/useCoreApi";

const i18n = I18N.of("ServerNotReachable")

let intervalID:any = undefined;
const clearIntervalInternal = () => {
  if(intervalID) {
    clearInterval(intervalID)
  }
}

const coreApi = useCoreApi()
const checkBackendState = () => {
  try {
    coreApi.api().getServerInfoApi().baseApiInfo().then(response => {
      if(response.status == 200) {
        const profile = response.data.profile
        if(profile) {
          clearIntervalInternal()
          console.log("Server up again")
          location.reload()
        }
      }
    })
  }catch (e) {
    console.log("Server not reachable")
  }
}

onMounted(() => {
  clearIntervalInternal()
  intervalID = setInterval(checkBackendState, 5000);
})

onUnmounted(() => clearIntervalInternal())

</script>

<style scoped>

.panel {
  margin-top: 5%;
  margin-left: 5%;
  margin-right: 5%;
}

</style>