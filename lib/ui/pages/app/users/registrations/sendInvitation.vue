<template>
  <HarmonyAuthenticatedPageHeader>
    <template v-slot:subHeader>
      <GenericBreadcrumbNavigation />
    </template>
    <template v-slot:actionArea>
      <harmony-button
          :caption="i18n.translate('Send').build()"
          @click="onSendClick"
          :is-in-loading-mode="isCurrentlySending"
          :disabled="!isInvitationAllowed"
      />
    </template>
  </HarmonyAuthenticatedPageHeader>
  <div v-if="isInvitationAllowed">
    <HarmonyViewModelForm
        :view-model-context="viewModelContext"
    >
      <harmony-panel>
        <form-text-field field-path="email" :caption="i18n.translate('E-Mail').build()"/>
        <form-text-area field-path="message" :caption="i18n.translate('Message').build()"/>
      </harmony-panel>
    </HarmonyViewModelForm>
  </div>
  <div v-else>
    <p>{{i18n.translate('The current workflow does not allow sending of invitations').build()}}</p>
  </div>
</template>

<script lang="ts" setup>

import HarmonyAuthenticatedPageHeader from "~/components/view/app/HarmonyAppViewPageHeader.vue";
import HarmonyButton from "@core/components/base/HarmonyButton.vue";
import {ref} from "vue";
import HarmonyPanel from "@core/components/base/HarmonyPanel.vue";
import I18N from "@core/utils/I18N";
import GenericBreadcrumbNavigation from "~/components/utils/GenericBreadcrumbNavigation.vue";
import HarmonyViewModelForm from "~/components/form/viewmodel/HarmonyViewModelForm.vue";
import FormTextField from "~/components/form/fields/FormTextField.vue";
import FormTextArea from "~/components/form/fields/FormTextArea.vue";
import useViewModelContext from "~/composables/form/useViewModelContext";
import useRouterUtils from "~/composables/useRouterUtils";
import useCoreApi from "~/composables/useCoreApi";
import {HarmonyAxiosResponse} from "~/utils/HarmonyTypes";

const i18n = I18N.of("sendInvitation")

const coreApi = useCoreApi()
const configurationResponse = await coreApi.api().getUserRegistrationApi().getConfiguration()
const isInvitationAllowed = ref(configurationResponse.data.isInvitationAllowed)

const viewModelContext = useViewModelContext("SendUserRegistrationInvitationVM")
const isCurrentlySending = viewModelContext.getReactiveIsSavingState()

const routerUtils = useRouterUtils()


const onSendClick = async () => {
  await viewModelContext.saveToApi()
}

viewModelContext.getContextConfig().afterSaveListeners.push({
  async afterSave(response: HarmonyAxiosResponse): Promise<void> {
    if (response.success) {
      routerUtils.goBack()
    }
  }
})



</script>

<style scoped>


</style>