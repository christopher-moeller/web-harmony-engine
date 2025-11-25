<template>
  <GenericResourceOverview :columns="columns" resource-name="i18nKeyEntries" ref="overview">
    <template #menuItems>
      <harmony-three-dots-menu-item :caption="i18n.translate('Import').build()" @click="isImportModalVisible = true"/>
      <harmony-three-dots-menu-item :caption="i18n.translate('Export').build()" @click="isExportModalVisible = true"/>
    </template>
    <template #filter="{filterContext, schemaData}">
      <div class="filter-row">
        <div class="filter-field-wrapper">
          <text-field-filter filter-key="classId" :caption="i18n.translate('Class ID').build()" :table-filter-context="filterContext" />
        </div>
        <div class="filter-field-wrapper">
          <text-field-filter filter-key="text" :caption="i18n.translate('Text').build()" :table-filter-context="filterContext" />
        </div>
        <div class="filter-field-wrapper">
          <select-field-filter filter-key="codeLocation" :caption="i18n.translate('Code Location').build()" :table-filter-context="filterContext" :schema-data="schemaData"/>
        </div>
      </div>
      <div class="filter-row">
        <div class="filter-field-wrapper">
          <boolean-field-filter filter-key="isCoreEntry" :caption="i18n.translate('Is Core Entry').build()" :table-filter-context="filterContext"/>
        </div>
        <div class="filter-field-wrapper">
          <boolean-field-filter filter-key="isProjectEntry" :caption="i18n.translate('Is Project Entry').build()" :table-filter-context="filterContext" />
        </div>
        <div class="filter-field-wrapper">
          <text-field-filter filter-key="key" :caption="i18n.translate('Key').build()" :table-filter-context="filterContext" />
        </div>
      </div>
    </template>
    <template #beforeTable>
      <p v-if="translationStatistics" class="statistics-row">{{i18n.translate("Translated Entries: {countOfTranslatedEntries}").add("countOfTranslatedEntries", translationStatistics.countOfTranslatedItems).build()}} {{i18n.translate("Not Translated Entries: {countOfNotTranslatedEntries}").add("countOfNotTranslatedEntries", translationStatistics.countOfNotTranslatedItems).build()}}</p>
    </template>
  </GenericResourceOverview>

  <harmony-modal v-model="isImportModalVisible" :is-minimal="true" :caption="i18n.translate('Import').build()">
    <div v-if="!fileUploadResponse">
      <HarmonyFileSelector :caption="i18n.translate('Select import json file').build()" v-model="selectedFilesToImport" :min="1" :max="1"/>
      <div style="display: flex; justify-content: center">
        <HarmonyButton :caption="i18n.translate('Upload').build()" @click="uploadImportFile" :disabled="selectedFilesToImport.length != 1"/>
      </div>
    </div>
    <div v-else>
      <p><b>{{i18n.translate('Included Elements').build()}}: </b> {{fileUploadResponse.data["importedElements"]}}</p>
      <p><b>{{i18n.translate('Ignored Elements').build()}}: </b> {{fileUploadResponse.data["ignoredElements"]}}</p>
      <div style="display: flex; justify-content: center; margin-top: 1em">
        <HarmonyButton :caption="i18n.translate('Close').build()" @click="onHideUploadDialog"/>
      </div>
    </div>
  </harmony-modal>

  <harmony-modal v-model="isExportModalVisible" :is-minimal="true" caption="Export">
    <div class="modal-row">
      <harmony-checkbox :caption="i18n.translate('Include Core Entries').build()" v-model="includeCoreEntriesForExport"/>
      <harmony-checkbox :caption="i18n.translate('Include Project Entries').build()" v-model="includeProjectEntriesForExport"/>
    </div>
    <div class="modal-row">
      <harmony-button :caption="i18n.translate('Export').build()" @click="onExportEntries"/>
    </div>
  </harmony-modal>
</template>

<script lang="ts" setup>

import GenericResourceOverview from "~/components/view/GenericResourceOverview.vue"
import TextFieldFilter from "@core/components/base/utils/filter/TextFieldFilter.vue";
import BooleanFieldFilter from "@core/components/base/utils/filter/BooleanFieldFilter.vue";
import SelectFieldFilter from "@core/components/base/utils/filter/SelectFieldFilter.vue";
import HarmonyThreeDotsMenuItem from "@core/components/base/HarmonyThreeDotsMenuItem.vue";
import HarmonyModal from "@core/components/base/HarmonyModal.vue";
import HarmonyButton from "@core/components/base/HarmonyButton.vue";
import HarmonyCheckbox from "@core/components/base/HarmonyCheckbox.vue";
import {ref} from "vue";
import I18N from "@core/utils/I18N";
import HarmonyFileSelector from "@core/components/base/HarmonyFileSelector.vue";
import {ECoreBackendPath, FileWebData, I18nTranslationStatistic} from "@core/CoreApi";
import useFileUtils from "~/composables/useFileUtils";
import useCoreApi from "~/composables/useCoreApi";
import {HarmonyTableColumn} from "~/utils/HarmonyTypes";

const i18n = I18N.of("keyEntries-index")

const columns:HarmonyTableColumn[] = [
  {
    id: "classId",
    caption: i18n.translate("Class ID").build()
  },
  {
    id: "key",
    caption: i18n.translate("Key").build()
  },
  {
    id: "codeLocation",
    caption: i18n.translate("Code Location").build()
  },
  {
    id: "isCoreEntry",
    caption: i18n.translate("Is Core Entry").build()
  },
  {
    id: "countOfTranslations",
    caption: i18n.translate("Count Of Translations").build()
  }
]

const isImportModalVisible = ref(false)
const isExportModalVisible = ref(false)

const includeCoreEntriesForExport = ref(true)
const includeProjectEntriesForExport = ref(true)

const fileUploadResponse = ref<any>()

const overview = ref()

const translationStatistics = ref<I18nTranslationStatistic>()

const selectedFilesToImport = ref<FileWebData[]>([])
watch(isImportModalVisible, () => selectedFilesToImport.value = [])

const uploadSuccessHandler = (response:any) => {
  fileUploadResponse.value = response
  overview.value.fetchCleanNewData()
}

const onHideUploadDialog = () => {
  fileUploadResponse.value = null;
}

const fileUtils = useFileUtils()
const onExportEntries = async () => {
  const queryParams = {includeCoreEntries: includeCoreEntriesForExport.value, includeSubprojectEntries: includeProjectEntriesForExport.value}
  await fileUtils.downloadFileFromApi(ECoreBackendPath.API_I18NKEYENTRIES_EXPORT, queryParams)
}

const coreApi = useCoreApi()
onMounted(async () => {
  translationStatistics.value = (await coreApi.api().getI18nKeyEntryApi().getStatistics()).data
})

const uploadImportFile = () => {
  if(selectedFilesToImport.value.length != 1) {
    console.error("No file selected")
    return;
  }

  const coreApi = useCoreApi()
  let fileToUpload:FileWebData = selectedFilesToImport.value[0];
  coreApi.api().getI18nKeyEntryApi().importKeyEntryData(fileToUpload)
      .then(uploadSuccessHandler)

}

</script>

<style scoped>

.filter-row {
  display: flex;
  flex-wrap: wrap;
}

.filter-field-wrapper {
  padding: 10px;
  width: 300px;
}

.modal-row {
  display: flex;
  justify-content: space-around;
  margin-top: 2em;
}

.statistics-row {
  color: var(--harmony-light-3);
  font-size: 14px;
}

</style>