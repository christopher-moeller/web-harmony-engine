<template>
  <HarmonyWebViewPageHeader :is-register-button-visible="false" />
  <harmony-view-model-form :view-model-context="viewModelContext">
    <div class="content">
      <harmony-panel caption="Reset Password" class="panel">
        <div v-if="!isSend" id="input-area">
          <form-text-field field-path="email" :caption="i18n.translate('E-Mail').build()"/>
          <div class="btn-row">
            <harmony-button :caption="i18n.translate('Reset').build()" class="reset-btn" @click="onSubmitClick" :is-in-loading-mode="isCurrentlySending"/>
          </div>
        </div>
        <div v-else id="response-are">
          <p>{{i18n.translate('If your address is correct you will receive a email to reset your password.').build()}}</p>
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

definePageMeta({
  isPublic: true
})

const i18n = I18N.of("forgot-password")

const isSend = ref(false)

const viewModelContext = useViewModelContext("UserPersonalAccountForgotPasswordVM")

const isCurrentlySending = viewModelContext.getReactiveIsSavingState()

const onSubmitClick = async () => {
  const response = await viewModelContext.saveToApi()
  if(response.success) {
    isSend.value = true
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