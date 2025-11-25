<template>
  <h3>{{ i18n.translate('Report Bug').build() }}</h3>
  <HarmonyTextField :caption="i18n.translate('Page').build()" v-model="currentPageInput"/>
  <HarmonyTextArea :caption="i18n.translate('Description').build()" v-model="descriptionInput" />
  <HarmonyFileSelector :caption="i18n.translate('Attachments').build()" v-model="attachments"/>
  <div id="button-area">
    <HarmonyButton :caption="i18n.translate('Report').build()" @click="onReportClick" :is-in-loading-mode="isCurrentlySending"/>
  </div>
</template>

<script setup lang="ts">
import HarmonyTextField from "@core/components/base/HarmonyTextField.vue";
import HarmonyTextArea from "@core/components/base/HarmonyTextArea.vue";
import HarmonyButton from "@core/components/base/HarmonyButton.vue";
import {FileWebData} from "@core/CoreApi";
import HarmonyFileSelector from "@core/components/base/HarmonyFileSelector.vue";
import useRouterUtils from "~/composables/useRouterUtils";
import useCoreApi from "~/composables/useCoreApi";

const i18n = I18N.of("ReportBugWindow");

const emit = defineEmits<{
  (e: 'onBugReported'): void
}>()

const currentPageInput = ref()
const descriptionInput = ref()

const isCurrentlySending = ref(false)

const attachments = ref<FileWebData[]>([])

const routerUtils = useRouterUtils()

onMounted(() => {
  currentPageInput.value =routerUtils.getCurrentRoute().value.fullPath
})

const coreApi = useCoreApi()
const onReportClick = () => {
  isCurrentlySending.value = true;
  coreApi.api().getReportUserErrorApi().reportUserBug({
    page: currentPageInput.value,
    description: descriptionInput.value,
    attachments: attachments.value
  }).finally(() => {
    isCurrentlySending.value = false
    emit('onBugReported')
  })
}

</script>

<style scoped>

#button-area {
  display: flex;
  justify-content: center;
}

</style>