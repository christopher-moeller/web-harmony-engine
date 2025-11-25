<template>
  <GenericResourceDetailView
      breadcrumb-label-v-m-attribute="email"
      resource-name="userRegistrations"
      :resource-context="resourceContext"
  >
    <div v-if="state === 'WAITING_FOR_ADMIN_CONFIRMATION'">
      <p> {{i18n.translate('The user is currently waiting for an admin confirmation').build()}}</p>
      <harmony-button :caption="i18n.translate('Confirm').build()" style="margin: 2em 0 2em 0" :is-in-loading-mode="confirmingProcessIsRunning" @click="onConfirmByAdminClick"/>
    </div>
    <form-text-field field-path="email" :caption="i18n.translate('E-Mail').build()"/>
    <form-select-field field-path="state" :caption="i18n.translate('State').build()" :singleSelection="true"></form-select-field>
    <form-text-area field-path="stateData" :caption="i18n.translate('State Data').build()"/>
  </GenericResourceDetailView>
</template>

<script lang="ts" setup>

import GenericResourceDetailView from "~/components/view/GenericResourceDetailView.vue"
import I18N from "@core/utils/I18N";
import {computed, ref} from "vue";
import HarmonyButton from "@core/components/base/HarmonyButton.vue";
import FormTextField from "~/components/form/fields/FormTextField.vue";
import FormSelectField from "~/components/form/fields/FormSelectField.vue";
import FormTextArea from "~/components/form/fields/FormTextArea.vue";
import useResourceContext from "~/composables/form/useResourceContext";
import useRouterUtils from "~/composables/useRouterUtils";
import useCoreApi from "~/composables/useCoreApi";

const i18n = I18N.of("registrations[id]")

const resourceContext = useResourceContext("userRegistrations", useRouterUtils().getResourceIdByUrl())

const state = resourceContext.getFieldValueRef("state")

const confirmingProcessIsRunning = ref(false)

const coreApi = useCoreApi()
const onConfirmByAdminClick = async () => {
  const id = resourceContext.getResourceId()
  confirmingProcessIsRunning.value = true
  await coreApi.api().getUserRegistrationApi().confirmByAdmin(id)
  confirmingProcessIsRunning.value = false
  await resourceContext.hardFetchApiState()
}

</script>

<style scoped>


</style>