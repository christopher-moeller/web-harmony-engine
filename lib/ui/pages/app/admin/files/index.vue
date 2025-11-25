<template>
  <GenericResourceOverview :columns="columns" ref="overview" resource-name="files">
    <template #filter="{filterContext}">
      <text-field-filter filter-key="search" :caption="i18n.translate('Search').build()" :table-filter-context="filterContext"/>
    </template>
    <template #actionArea>
      <HarmonyButton :caption="i18n.translate('Upload').build()" @click="openUploadModal"/>
    </template>
  </GenericResourceOverview>
  <HarmonyModal v-model="isUploadModalOpen" :is-minimal="true">
    <h3 style="margin-bottom: 3em">{{ i18n.translate('Upload new file').build() }}</h3>
    <HarmonyFileSelector :caption="i18n.translate('File to upload').build()" v-model="filesToUpload"/>
    <div style="display: flex; justify-content: center;">
      <HarmonyButton :caption="i18n.translate('Upload').build()" @click="onUploadClick"/>
    </div>
  </HarmonyModal>
</template>

<script lang="ts" setup>

import GenericResourceOverview from "~/components/view/GenericResourceOverview.vue"
import I18N from "@core/utils/I18N";
import HarmonyButton from "@core/components/base/HarmonyButton.vue";
import HarmonyModal from "@core/components/base/HarmonyModal.vue";
import {ref} from "vue";
import HarmonyFileSelector from "@core/components/base/HarmonyFileSelector.vue";
import {FileWebData} from "@core/CoreApi";
import TextFieldFilter from "~/components/base/utils/filter/TextFieldFilter.vue";
import useCoreApi from "~/composables/useCoreApi";
import {HarmonyTableColumn} from "~/utils/HarmonyTypes";

const isUploadModalOpen = ref(false)

const filesToUpload = ref<FileWebData[]>([])

const overview = ref()

const i18n = I18N.of("files-index")

const columns:HarmonyTableColumn[] = [
  {
    id: "fileName",
    caption: i18n.translate('Filename').build()
  },
  {
    id: "isTemp",
    caption: i18n.translate('Is Temporary').build(),
  },
  {
    id: "createdAt",
    caption: i18n.translate('Created At').build(),
  },
  {
    id: "createdBy",
    caption: i18n.translate('Created By').build(),
  }
]

const onUploadSuccess = () => {
  isUploadModalOpen.value = false;
  overview.value.fetchCleanNewData()
}

const coreApi = useCoreApi()
const onUploadClick = () => {
  coreApi.api().getFileApi().handleFileUpload(filesToUpload.value)
      .then(onUploadSuccess)
      .finally(() => isUploadModalOpen.value = false)
}

const openUploadModal = () => {
  filesToUpload.value = [];
  isUploadModalOpen.value = true;
}

</script>

<style scoped>

</style>