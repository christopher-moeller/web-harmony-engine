<template>
  <HarmonyWebViewPageHeader
      :is-login-button-visible="false"
  />
  <HarmonyViewModelForm
      :view-model-context="viewModelContext"
  >
    <div class="content">
      <form id="login" @submit.prevent>
        <harmony-panel caption="Login">
          <form-text-field field-path="username" :caption="i18n.translate('E-Mail').build()" autocomplete-name="email" @onEnterPressed="onLoginClick"/>
          <form-text-field field-path="password"  :caption="i18n.translate('Password').build()" :is-password="true" autocomplete-name="current-password" @onEnterPressed="onLoginClick"/>
          <HarmonyLink style="margin-top: 2em" :page="ECoreRouterPage.FORGOTPASSWORD" :caption="i18n.translate('Forgot Password').build()"/>
          <div class="btn-row">
            <harmony-button :caption="i18n.translate('Login').build()" class="login-btn" @click="onLoginClick" :is-in-loading-mode="isCurrentlySending"/>
          </div>
        </harmony-panel>
      </form>
    </div>
  </HarmonyViewModelForm>
</template>

<script lang="ts" setup>

import HarmonyButton from "~/components/base/HarmonyButton.vue"
import HarmonyPanel from "~/components/base/HarmonyPanel.vue";
import I18N from "~/utils/I18N";
import HarmonyLink from "~/components/base/HarmonyLink.vue";
import HarmonyWebViewPageHeader from "~/components/view/web/HarmonyWebViewPageHeader.vue";
import {ECoreActorRight, ECoreRouterPage} from "~/CoreApi";
import HarmonyViewModelForm from "~/components/form/viewmodel/HarmonyViewModelForm.vue";
import useViewModelContext from "~/composables/form/useViewModelContext";
import FormTextField from "~/components/form/fields/FormTextField.vue";
import useRouterUtils from "~/composables/useRouterUtils";
import useAuthenticationContext from "~/composables/useAuthenticationContext";
import {HarmonyAxiosResponse} from "~/utils/HarmonyTypes";

definePageMeta({
  isPublic: true
})


const i18n = I18N.of("login")
const viewModelContext = useViewModelContext("UserLoginVM")

const isCurrentlySending = viewModelContext.getReactiveIsSavingState()

const onLoginClick = async () => {
  await viewModelContext.saveToApi()
}

const routerUtils = useRouterUtils()
const authenticationContext = useAuthenticationContext()

const redirectAfterLogin = async () => {
  const query = routerUtils.getCurrentRoute().value.query

  const hasAppAccess:boolean = await authenticationContext.hasRight(ECoreActorRight.CORE_APP_ACCESS)
  const redirectURL:string | undefined = query.redirectURL?.toString()

  if(redirectURL) {
    if(redirectURL.startsWith("/app")) {
      if(hasAppAccess) {
        await routerUtils.hardNavigateToPath(redirectURL)
      } else {
        await routerUtils.hardNavigateToPage(ECoreRouterPage.INDEX)
      }
    } else {
      await routerUtils.hardNavigateToPath(redirectURL)
    }
  } else {
    if(hasAppAccess) {
      await routerUtils.hardNavigateToPage(ECoreRouterPage.APP_HOME)
    } else {
      await routerUtils.hardNavigateToPage(ECoreRouterPage.INDEX)
    }
  }
}

viewModelContext.getContextConfig().afterSaveListeners.push({
  async afterSave(response: HarmonyAxiosResponse) {
    if(response.success) {
      authenticationContext.setToken(response.data.token)
      await redirectAfterLogin()
    }
  }
})
</script>

<style scoped>

#login {
  width: 100%;
  max-width: 40em;
}

.content {
  display: flex;
  justify-content: center;
  align-items: center;
  margin-top: 2em;
}


.btn-row {
  display: flex;
  align-items: center;
  justify-content: center;
}

.login-btn {
  margin-top: 30px;
  height: 40px;
  width: 10rem;
}

</style>