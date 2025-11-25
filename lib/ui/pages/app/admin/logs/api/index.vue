<template>
  <div class="container">
    <HarmonyAuthenticatedPageHeader>
      <template #actionArea>
        <HarmonyButton :caption="i18n.translate('Reload').build()" @click="onReloadClick" :is-in-loading-mode="isReloading"/>
      </template>
      <template #subHeader>
        <GenericBreadcrumbNavigation />
      </template>
    </HarmonyAuthenticatedPageHeader>
    <div id="log-terminal">
      <p v-for="(logLine, index) in logLines" :key="index" class="output">
        {{logLine}}
      </p>
    </div>
  </div>
</template>

<script lang="ts" setup>

import HarmonyAuthenticatedPageHeader from "~/components/view/app/HarmonyAppViewPageHeader.vue";
import I18N from "~/utils/I18N";
import HarmonyButton from "~/components/base/HarmonyButton.vue";
import {ref} from "vue";
import GenericBreadcrumbNavigation from "~/components/utils/GenericBreadcrumbNavigation.vue";
import useCoreApi from "~/composables/useCoreApi";

const i18n = I18N.of("logs-index")

const isReloading = ref(false)

const logLines = ref<String[]>([])
const coreApi = useCoreApi()
const loadEntries = async () => {
  isReloading.value = true;
  const response = await coreApi.api().getLogApi().getAllLogs()
  logLines.value = response.data
  isReloading.value = false;
}

onMounted(() => {
  loadEntries()
})

const onReloadClick = () => {
  loadEntries()
}


</script>

<style scoped>

.container {
  display: flex;
  flex-flow: column;
  height: 100%;
}

#log-terminal {
  background-color: #1e1e1e;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  white-space: pre-wrap;
  overflow-x: scroll;
  height: 100%;
  margin: 1em;
}

.output {
  color: #abb2bf;
}

</style>