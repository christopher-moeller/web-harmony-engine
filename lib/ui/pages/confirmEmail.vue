<template>
  <HarmonyWebViewPageHeader :is-register-button-visible="false"/>
  <client-only>
    <div v-if="!isEmailConfirmationAllowed">
      <harmony-panel :caption="i18n.translate('Not allowed').build()" class="panel">
        <p>{{i18n.translate('It is currently not allowed to create a new account.').build()}}</p>
        <div class="btn-row">
          <harmony-button :caption="i18n.translate('Homepage').build()" class="action-btn" @click="onBackToHomepageClick" />
        </div>
      </harmony-panel>
    </div>
    <div v-else>
      <harmony-panel :caption="i18n.translate('Confirm E-Mail').build()" class="panel">
        <div v-if="currentState === 'WAITING_BACKEND_CONFIRMATION'">
          <p>{{i18n.translate('Confirmation in process...').build()}}</p>
        </div>
        <div v-else-if="currentState === 'CONFIRMATION_SUCCESSFUL'">
          <p>{{i18n.translate('Confirmation was successful.').build()}}</p>
          <div v-if="isAdminConfirmationRequired">
            <p>{{i18n.translate('Please wait for the admin confirmation.').build()}}</p>
            <div class="btn-row">
              <harmony-button :caption="i18n.translate('Homepage').build()" class="action-btn" @click="onBackToHomepageClick" />
            </div>
          </div>
          <div v-else>
            <p>{{i18n.translate('You are now allowed to login to the application.').build()}}</p>
            <div class="btn-row">
              <harmony-button :caption="i18n.translate('Login').build()" class="action-btn" style="margin-right: 2em" @click="onToLoginClick" />
              <harmony-button :caption="i18n.translate('Homepage').build()" class="action-btn" :type="HarmonyButtonType.SECONDARY" @click="onBackToHomepageClick" />
            </div>
          </div>
        </div>
        <div v-else-if="currentState === 'CONFIRMATION_ERROR'">
          <p>{{i18n.translate('Unfortunately something went wrong. Please get in contact with the administrator of this application.').build()}}</p>
          <div class="btn-row">
            <harmony-button :caption="i18n.translate('Homepage').build()" class="action-btn" @click="onBackToHomepageClick" />
          </div>
        </div>
      </harmony-panel>
    </div>
  </client-only>
</template>

<script lang="ts" setup>

import I18N from "~/utils/I18N";
import {ref} from "vue";
import HarmonyPanel from "~/components/base/HarmonyPanel.vue";
import HarmonyButton from "~/components/base/HarmonyButton.vue";
import {ECoreRouterPage} from "~/CoreApi";
import HarmonyWebViewPageHeader from "~/components/view/web/HarmonyWebViewPageHeader.vue";
import {HarmonyButtonType} from "~/utils/HarmonyTypes";
import useRouterUtils from "~/composables/useRouterUtils";
import useCoreApi from "~/composables/useCoreApi";

definePageMeta({
  isPublic: true
})

const i18n = I18N.of("confirmEmail")

const currentState = ref("WAITING_BACKEND_CONFIRMATION")

const coreApi = useCoreApi()
const registrationConfiguration = (await coreApi.api().getUserRegistrationApi().getConfiguration()).data
const isEmailConfirmationAllowed = registrationConfiguration.isEmailConfirmationAllowed

const isAdminConfirmationRequired = registrationConfiguration.isAdminConfirmationRequired

const routerUtils = useRouterUtils()
const token = routerUtils.getCurrentRoute().value.query.token

onMounted(async () => {
  if(process.client) {
    const response = await coreApi.api().getUserRegistrationApi().confirmEmail({token: token+""})
    if(response.status === 200) {
      currentState.value = "CONFIRMATION_SUCCESSFUL"
    } else {
      currentState.value = "CONFIRMATION_ERROR"
    }
  }
})

const onBackToHomepageClick = () => routerUtils.softNavigateToPage(ECoreRouterPage.INDEX)
const onToLoginClick = () => routerUtils.softNavigateToPath(ECoreRouterPage.LOGIN)

</script>

<style scoped>

.panel {
  margin-left: 5%;
  margin-right: 5%;
}

.btn-row {
  display: flex;
  align-items: center;
  justify-content: center;
}

.action-btn {
  margin-top: 30px;
  height: 40px;
  width: 10rem;
}

</style>