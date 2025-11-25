<template>
  <HarmonyAuthenticatedPageHeader>
    <template #actionArea>
      <SaveAndRevertButton :is-dirty="isDirty" :is-currently-saving="isSaving" @onSave="onSaveClick" @onRevertChanges="onRevertChanges"/>
      <HarmonyButton :disabled="isDirty" style="margin-left: 1em" :caption="i18n.translate('Reset').build()" @click="onResetClick" :is-in-loading-mode="isResetting"/>
    </template>
    <template #subHeader>
      <GenericBreadcrumbNavigation />
    </template>
  </HarmonyAuthenticatedPageHeader>
  <HarmonyPanel>
    <harmony-view-model-form :view-model-context="viewModelContext" ref="vmForm" :reload-after-save="true">
      <form-text-area field-path="userMessage" :caption="i18n.translate('User Message').build()"/>
      <form-text-area field-path="technicalMessage" :caption="i18n.translate('Technical Message').build()"/>
      <form-select-field :caption="i18n.translate('Status').build()" field-path="status" :single-selection="true"/>
    </harmony-view-model-form>
  </HarmonyPanel>
</template>

<script lang="ts" setup>

import HarmonyAuthenticatedPageHeader from "~/components/view/app/HarmonyAppViewPageHeader.vue";
import I18N from "@core/utils/I18N";
import HarmonyPanel from "@core/components/base/HarmonyPanel.vue";
import HarmonyButton from "@core/components/base/HarmonyButton.vue";
import {ref} from "vue";
import SaveAndRevertButton from "@core/components/utils/SaveAndRevertButton.vue";
import GenericBreadcrumbNavigation from "~/components/utils/GenericBreadcrumbNavigation.vue";
import HarmonyViewModelForm from "~/components/form/viewmodel/HarmonyViewModelForm.vue";
import FormTextArea from "~/components/form/fields/FormTextArea.vue";
import FormSelectField from "~/components/form/fields/FormSelectField.vue";
import useViewModelContext from "~/composables/form/useViewModelContext";
import useRouterUtils from "~/composables/useRouterUtils";
import useBannerContext from "~/composables/useBannerContext";
import useCoreApi from "~/composables/useCoreApi";

const i18n = I18N.of("applicationStatus-index")

const vmForm = ref()

const isResetting = ref(false)

const viewModelContext = useViewModelContext("ApplicationStatusVM")
const isDirty = viewModelContext.getReactiveDirtyState()
const isSaving = viewModelContext.getReactiveIsSavingState()

const bannerContext = useBannerContext()
const onSaveClick = async () => {
  const response = await viewModelContext.saveToApi()
  if(response.success) {
    bannerContext.pushShortSuccessBanner(i18n.translate("Status successfully updated").build())
  }
}

const onRevertChanges = () => {
  viewModelContext.revertChanges()
}
const routerUtils = useRouterUtils()
const coreApi = useCoreApi()
const onResetClick = async () => {
  isResetting.value = true
  await coreApi.api().getApplicationStatusApi().resetStatus()
  isResetting.value = false
  await vmForm.value.reloadForm()
  routerUtils.reloadCurrentPage()
}


</script>

<style scoped>

</style>