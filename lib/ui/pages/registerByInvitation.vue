<template>
  <HarmonyWebViewPageHeader :is-register-button-visible="false" />
  <div v-if="!isRegistrationByInvitationAllowed">
    <harmony-panel :caption="i18n.translate('Not allowed').build()" class="panel">
      <p>{{i18n.translate('It is currently not allowed to create a new account.').build()}}</p>
      <div class="btn-row">
        <harmony-button :caption="i18n.translate('Homepage').build()" class="login-btn" @click="onBackToHomepageClick" />
      </div>
    </harmony-panel>
  </div>
  <div v-else>
    <harmony-view-model-form
        v-if="currentState === 'WAITING_FOR_USER_INPUT'"
        :view-model-context="viewModelContext"
        :load-link-resolver="apiLinkResolver"
        :save-link-resolver="apiLinkResolver"
    >
      <div class="content">
        <form id="register" @submit.prevent="onSubmitClick" style="width: 100%; max-width: 40em;">
          <harmony-panel caption="Register" class="panel">
            <form-text-field field-path="email" :caption="i18n.translate('E-Mail').build()" autocomplete-name="email"/>
            <form-text-field field-path="firstname" :caption="i18n.translate('Firstname').build()" autocomplete-name="firstname"/>
            <form-text-field field-path="lastname" :caption="i18n.translate('Lastname').build()" autocomplete-name="lastname"/>
            <form-text-field field-path="password"  :caption="i18n.translate('Password').build()" :is-password="true"/>
            <form-text-field field-path="passwordAgain"  :caption="i18n.translate('Password again').build()" :is-password="true"/>
            <div class="btn-row">
              <harmony-button :caption="i18n.translate('Submit').build()" class="login-btn" @click="onSubmitClick" :is-in-loading-mode="isCurrentlySending"/>
            </div>
          </harmony-panel>
        </form>
      </div>
    </harmony-view-model-form>
    <div v-else-if="currentState === 'SUCCESS'">
      <harmony-panel :caption="i18n.translate('Thank you for signing up').build()" class="panel">
        <p>{{i18n.translate('Pleas confirm that this is your E-Mail by clicking on the confirmation link.').build()}}</p>
        <p><b>{{submittedUserInputData ? submittedUserInputData['email'] : '?'}}</b></p>
        <div class="btn-row">
          <harmony-button :caption="i18n.translate('Homepage').build()" class="login-btn" @click="onBackToHomepageClick" />
        </div>
      </harmony-panel>
    </div>
  </div>
</template>

<script lang="ts" setup>
import HarmonyButton from "~/components/base/HarmonyButton.vue";
import HarmonyPanel from "~/components/base/HarmonyPanel.vue";
import {ref} from "vue";
import I18N from "~/utils/I18N";
import {ApiLink, ECoreRouterPage} from "~/CoreApi";
import HarmonyWebViewPageHeader from "~/components/view/web/HarmonyWebViewPageHeader.vue";
import HarmonyViewModelForm from "~/components/form/viewmodel/HarmonyViewModelForm.vue";
import FormTextField from "~/components/form/fields/FormTextField.vue";
import useViewModelContext from "~/composables/form/useViewModelContext";
import {ApiLinkResolver, EffectiveApiLink} from "~/utils/HarmonyTypes";
import useRouterUtils from "~/composables/useRouterUtils";
import useCoreApi from "~/composables/useCoreApi";

definePageMeta({
  isPublic: true
})

const i18n = I18N.of("registerByInvitation")

const coreApi = useCoreApi()
const isRegistrationByInvitationAllowed = (await coreApi.api().getUserRegistrationApi().getConfiguration()).data.isRegistrationByInvitationAllowed

const currentState = ref("WAITING_FOR_USER_INPUT")

const viewModelContext = useViewModelContext("UserRegistrationWithInvitationVM")
const isCurrentlySending = viewModelContext.getReactiveIsSavingState()

const submittedUserInputData = ref()

const routerUtils = useRouterUtils()
const token = routerUtils.getCurrentRoute().value.query.token

const apiLinkResolver:ApiLinkResolver = {
  resolveApiLink(apiLink: ApiLink): EffectiveApiLink {
    return {
      link: apiLink.link?.replace("{tokenValue}", token+"")!,
      requestMethod: apiLink.requestMethod!
    }
  }
}

const onSubmitClick = async (e:any) => {

  if(!isRegistrationByInvitationAllowed)
    throw new Error("Registration from scratch is not allowed")

  if(e) {
    e.preventDefault()
    return
  }

  // @ts-ignore
  const response = await viewModelContext.saveToApi()
  if(response.success) {
    submittedUserInputData.value = response.axiosRequestConfig.data
    currentState.value = "SUCCESS"
  }
}

const onBackToHomepageClick = () => routerUtils.softNavigateToPage(ECoreRouterPage.INDEX)

</script>

<style scoped>

.content {
  display: flex;
  align-items: center;
  justify-content: center;
}

.panel {
  margin-left: 5%;
  margin-right: 5%;
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