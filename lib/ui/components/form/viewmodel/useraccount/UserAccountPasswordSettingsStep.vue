<template>
  <HarmonyPanel :caption="i18n.translate('Password').build()" class="panel">
    <template #action>
      <SaveAndRevertButton
          :is-currently-saving="isCurrentlySaving"
          :is-dirty="isDirty"
          @onRevertChanges="viewModelContext.revertChanges"
          @onSave="onSaveClick"
      />
    </template>
    <template #default>
      <HarmonyViewModelForm
          :view-model-context="viewModelContext"
          :reload-after-save="true"
      >
        <form-text-field :caption="i18n.translate('Old Password').build()" field-path="oldPassword" :is-password="true"/>
        <form-text-field :caption="i18n.translate('New Password').build()" field-path="newPassword" :is-password="true"/>
        <form-text-field :caption="i18n.translate('New Password Again').build()" field-path="newPasswordAgain" :is-password="true"/>
      </HarmonyViewModelForm>
      <HarmonyButton caption="Back" @click="onBackToGeneral"/>
    </template>
  </HarmonyPanel>
</template>

<script setup lang="ts">

import SaveAndRevertButton from "~/components/utils/SaveAndRevertButton.vue";
import HarmonyViewModelForm from "~/components/form/viewmodel/HarmonyViewModelForm.vue";
import HarmonyPanel from "~/components/base/HarmonyPanel.vue";
import HarmonyButton from "~/components/base/HarmonyButton.vue";
import useViewModelContext from "~/composables/form/useViewModelContext";
import FormTextField from "~/components/form/fields/FormTextField.vue";
import useBannerContext from "~/composables/useBannerContext";

const i18n = I18N.of("UserAccountPasswordSettingsStep")

const viewModelContext = useViewModelContext("UserPersonalAccountChangePasswordVM")
const isDirty = viewModelContext.getReactiveDirtyState()
const isCurrentlySaving = viewModelContext.getReactiveIsSavingState()

const emit = defineEmits<{
  (e: 'onBackToGeneral'): void
}>()

const bannerContext = useBannerContext()
const onBackToGeneral = () => emit('onBackToGeneral')
const onSaveClick = async () => {
  const saveResponse = await viewModelContext.saveToApi()
  if(saveResponse.success) {
    onBackToGeneral()
    bannerContext.pushShortSuccessBanner(i18n.translate("Password successfully updated").build())
  }
}


</script>

<style scoped>

.panel {
  width: 50em;
}

</style>