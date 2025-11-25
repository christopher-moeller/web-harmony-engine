<template>
  <HarmonyWebViewPageHeader :is-register-button-visible="false" />
  <harmony-view-model-form
      :view-model-context="viewModelContext"
      :save-link-resolver="apiLinkResolver"
      :load-link-resolver="apiLinkResolver"
  >
    <div class="content">
      <harmony-panel caption="Reset Password" class="panel">
        <div v-if="!isSuccessfullySend" id="input-area">
          <form-text-field field-path="email" :caption="i18n.translate('E-Mail').build()"/>
          <form-text-field :caption="i18n.translate('New Password').build()" field-path="password" :is-password="true"/>
          <form-text-field :caption="i18n.translate('New Password Again').build()" field-path="passwordAgain" :is-password="true"/>
          <div class="btn-row">
            <harmony-button :caption="i18n.translate('Reset').build()" class="reset-btn" @click="onSubmitClick" :is-in-loading-mode="isCurrentlySending"/>
          </div>
        </div>
        <div v-else id="response-are">
          <p>{{i18n.translate('You can now use your new password').build()}}</p>
        </div>
      </harmony-panel>
    </div>
  </harmony-view-model-form>
</template>

<script setup lang="ts">

import HarmonyButton from "~/components/base/HarmonyButton.vue";
import HarmonyPanel from "~/components/base/HarmonyPanel.vue";
import I18N from "~/utils/I18N";
import {ref} from "vue";
import HarmonyWebViewPageHeader from "~/components/view/web/HarmonyWebViewPageHeader.vue";
import HarmonyViewModelForm from "~/components/form/viewmodel/HarmonyViewModelForm.vue";
import FormTextField from "~/components/form/fields/FormTextField.vue";
import useViewModelContext from "~/composables/form/useViewModelContext";
import {ApiLink} from "~/CoreApi";
import {ApiLinkResolver, EffectiveApiLink} from "~/utils/HarmonyTypes";
import useRouterUtils from "~/composables/useRouterUtils";

definePageMeta({
  isPublic: true
})

const i18n = I18N.of("resetPasswordByEmail")

const routerUtils = useRouterUtils()
const token = useRouterUtils().getCurrentRoute().value.query.token

const apiLinkResolver:ApiLinkResolver = {
  resolveApiLink(apiLink: ApiLink): EffectiveApiLink {
    return {
      link: apiLink.link!.replace("{tokenValue}", token+""),
      requestMethod: apiLink.requestMethod!
    }
  }
}

const viewModelContext = useViewModelContext("UserPersonalAccountResetPasswordByEmailVM")
const isCurrentlySending = viewModelContext.getReactiveIsSavingState()
const isSuccessfullySend = ref(false)

const onSubmitClick = async () => {
  const response = await viewModelContext.saveToApi()
  if(response.success) {
    isSuccessfullySend.value = true
  }
}

</script>

<style scoped>

.content {
  display: flex;
  align-items: center;
  justify-content: center;
}

.panel {
  width: 80%;
  min-width: 300px;
  max-width: 600px;
}

.btn-row {
  display: flex;
  align-items: center;
  justify-content: center;
}

.reset-btn {
  margin-top: 30px;
  height: 40px;
  width: 10rem;
}

</style>