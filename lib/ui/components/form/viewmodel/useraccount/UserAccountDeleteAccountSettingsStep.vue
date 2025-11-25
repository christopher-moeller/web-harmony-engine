<template>
  <HarmonyPanel :caption="i18n.translate('Delete Account').build()" class="panel">
    <HarmonyViewModelForm
        :view-model-context="viewModelContext"
        :reload-after-save="true"
    >
      <p>{{i18n.translate('If you really want to delete your account. Please enter your password below.').build()}}</p>
      <form-text-field :caption="i18n.translate('Password').build()" field-path="password" :is-password="true"/>
    </HarmonyViewModelForm>
    <div class="action-button-row">
      <HarmonyButton caption="Back" @click="onBackToGeneral"/>
      <harmony-button caption="Delete" :type="HarmonyButtonType.DANGER" @click="onDeleteClick" />
    </div>
  </HarmonyPanel>
</template>

<script setup lang="ts">

import HarmonyPanel from "~/components/base/HarmonyPanel.vue";
import HarmonyButton from "~/components/base/HarmonyButton.vue";
import useViewModelContext from "~/composables/form/useViewModelContext";
import HarmonyViewModelForm from "~/components/form/viewmodel/HarmonyViewModelForm.vue";
import FormTextField from "~/components/form/fields/FormTextField.vue";
import useRouterUtils from "~/composables/useRouterUtils";
import {ECoreRouterPage} from "~/CoreApi";
import useAuthenticationContext from "~/composables/useAuthenticationContext";
import {HarmonyButtonType} from "~/utils/HarmonyTypes";

const i18n = I18N.of("UserAccountDeleteAccountSettingsStep")

const viewModelContext = useViewModelContext("UserPersonalAccountDeleteVM")
const isDirty = viewModelContext.getReactiveDirtyState()


const emit = defineEmits<{
  (e: 'onBackToGeneral'): void
}>()
const onBackToGeneral = () => emit('onBackToGeneral')

const routerUtils = useRouterUtils()
const authenticationContext =  useAuthenticationContext();

const onDeleteClick = async () => {
  const response = await viewModelContext.saveToApi()
  if(response.success) {
    await authenticationContext.logoutAndClear()
    await routerUtils.hardNavigateToPage(ECoreRouterPage.INDEX)
  }
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