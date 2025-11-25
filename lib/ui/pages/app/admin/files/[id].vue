<template>
  <GenericResourceDetailView breadcrumb-label-v-m-attribute="fileName" resource-name="files" :resource-context="resourceContext">
    <template v-slot:default>
      <form-text-field field-path="fileName" :caption="i18n.translate('File Name').build()"/>
      <form-checkbox field-path="isTemp" :caption="i18n.translate('Is temporary').build()"/>
    </template>
    <template v-slot:additionalMenuItems>
      <harmony-three-dots-menu-item :caption="i18n.translate('Download').build()" @onItemClick="onDownloadClick"/>
    </template>
  </GenericResourceDetailView>
</template>

<script lang="ts" setup>

import GenericResourceDetailView from "~/components/view/GenericResourceDetailView.vue"
import HarmonyThreeDotsMenuItem from "@core/components/base/HarmonyThreeDotsMenuItem.vue";
import I18N from "@core/utils/I18N";
import FormTextField from "~/components/form/fields/FormTextField.vue";
import FormCheckbox from "~/components/form/fields/FormCheckbox.vue";
import useResourceContext from "~/composables/form/useResourceContext";
import useRouterUtils from "~/composables/useRouterUtils";
import {ECoreBackendPath} from "~/CoreApi";
import useFileUtils from "~/composables/useFileUtils";

const i18n = I18N.of("files[id]")

const resourceContext = useResourceContext("files", useRouterUtils().getResourceIdByUrl())

const fileUtils = useFileUtils()
const onDownloadClick = async () => {
  await fileUtils.downloadFileFromApi(ECoreBackendPath.API_FILES_UUID_DOWNLOAD.replace("{uuid}", resourceContext.getResourceId()))
}


</script>

<style scoped>


</style>