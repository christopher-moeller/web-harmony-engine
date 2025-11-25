<template>
    <HarmonyPanel :caption="i18n.translate('General').build()" class="panel">
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
          <form-text-field field-path="email" :caption="i18n.translate('E-Mail').build()"/>
          <form-text-field field-path="firstname" :caption="i18n.translate('Firstname').build()" />
          <form-text-field field-path="lastname" :caption="i18n.translate('Firstname').build()"/>
        </HarmonyViewModelForm>
        <div class="action-button-row">
          <HarmonyButton caption="Change Password" @click="onChangePasswordClick"/>
          <HarmonyButton caption="Delete Account" :type="HarmonyButtonType.DANGER" @click="onDeleteAccountClick"/>
        </div>
      </template>
    </HarmonyPanel>
</template>

<script setup lang="ts">

import HarmonyPanel from "~/components/base/HarmonyPanel.vue";
import SaveAndRevertButton from "~/components/utils/SaveAndRevertButton.vue";
import HarmonyViewModelForm from "~/components/form/viewmodel/HarmonyViewModelForm.vue";
import HarmonyButton from "~/components/base/HarmonyButton.vue";
import useViewModelContext from "~/composables/form/useViewModelContext";
import FormTextField from "~/components/form/fields/FormTextField.vue";
import useBannerContext from "~/composables/useBannerContext";
import {HarmonyButtonType} from "~/utils/HarmonyTypes";

const i18n = I18N.of("UserAccountGeneralSettingsStep")

const viewModelContext = useViewModelContext("UserPersonalAccountVM")
const isDirty = viewModelContext.getReactiveDirtyState()
const isCurrentlySaving = viewModelContext.getReactiveIsSavingState()

const emit = defineEmits<{
  (e: 'onChangePasswordClick'): void,
  (e: 'onDeleteAccountClick'): void
}>()

const bannerContext = useBannerContext()

const onSaveClick = async () => {
  const saveResponse = await viewModelContext.saveToApi()
  if(saveResponse.success) {
    bannerContext.pushShortSuccessBanner(i18n.translate("Account successfully updated").build())
  }
}

const onChangePasswordClick = () => {
  emit('onChangePasswordClick')
}

const onDeleteAccountClick = () => {
  emit('onDeleteAccountClick')
}


</script>

<style scoped>

.panel {
  width: 50em;
}

.action-button-row {
  display: flex;
  justify-content: space-between;
  flex-wrap: wrap;
  gap: 1em;
}


</style>