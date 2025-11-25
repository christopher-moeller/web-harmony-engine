<template>
  <HarmonyModal v-model="computedModel">
    <template v-if="userIsAuthenticated">
      <div>
        <p class="name-title"><span>Hello</span> {{userDto!.firstname}}</p>
        <div v-if="hasRightToChangeOwnUserAccount">
          <HarmonyLink :page="ECoreRouterPage.ACCOUNT" :caption="i18n.translate('Edit Profile').build()"/>
        </div>
      </div>
      <div class="centered-row">
        <HarmonyButton
            v-if="hasRightToAccessApp"
            class="buttons"
            :caption="i18n.translate('Open App').build()"
            @click="onOpenAppClicked"
        />
        <HarmonyButton
            class="buttons"
            :caption="i18n.translate('Logout').build()"
            @click="onLogoutClicked"
        />
      </div>
    </template>
    <template v-else>
      <div class="centered-row">
        <HarmonyButton
            class="buttons"
            :caption="i18n.translate('Login').build()"
            @click="onLoginClicked"
        />
      </div>
      <div v-if="isRegistrationAllowed" class="centered-row">
        <p>{{i18n.translate('No Account?').build()}}</p>
        <HarmonyLink :page="ECoreRouterPage.REGISTER" :caption="i18n.translate('Please a create a new account here').build()"/>
      </div>
    </template>
    <div class="link-block-container">
      <div class="link-block">
        <div>
          <HarmonyLink :page="ECoreRouterPage.ABOUT" :caption="i18n.translate('About').build()"/>
        </div>
        <div>
          <HarmonyLink :page="ECoreRouterPage.IMPRINT" :caption="i18n.translate('Imprint').build()"/>
        </div>
        <div>
          <HarmonyLink :page="ECoreRouterPage.PRIVACYNOTICE" :caption="i18n.translate('Privacy Notice').build()"/>
        </div>
        <div>
          <HarmonyLink :page="ECoreRouterPage.LANGUAGE" :caption="i18n.translate('Language').build()"/>
        </div>
      </div>
    </div>
  </HarmonyModal>
</template>

<script setup lang="ts">

import {ComputedUtils} from "~/utils/ComputedUtils";
import HarmonyModal from "~/components/base/HarmonyModal.vue";
import useAuthenticationContext from "~/composables/useAuthenticationContext";
import HarmonyButton from "~/components/base/HarmonyButton.vue";
import {AuthenticatedUserDto, ECoreActorRight, ECoreRouterPage} from "~/CoreApi";
import HarmonyLink from "~/components/base/HarmonyLink.vue";
import useRouterUtils from "~/composables/useRouterUtils";
import useCoreApi from "~/composables/useCoreApi";

const i18n = I18N.of("HarmonyUserWebModal")

const props = defineProps({
  modelValue: {
    type: Boolean,
    required: true
  }
})

const computedModel = ComputedUtils.createDelegatedComputedValue(props, getCurrentInstance()!)

const userIsAuthenticated = await useAuthenticationContext().getIsAuthenticated()

const userDto:AuthenticatedUserDto | undefined = userIsAuthenticated ? await useAuthenticationContext().getUser() : undefined

const coreApi = useCoreApi()
const registrationConfigurationResponse = await coreApi.api().getUserRegistrationApi().getConfiguration()
const isRegistrationAllowed = registrationConfigurationResponse.data.isRegistrationFromScratchAllowed

const hasRightToChangeOwnUserAccount = userIsAuthenticated && await useAuthenticationContext().hasRight(ECoreActorRight.CORE_USER_PERSONAL_ACCOUNT_CRUD)
const hasRightToAccessApp = userIsAuthenticated && await useAuthenticationContext().hasRight(ECoreActorRight.CORE_APP_ACCESS)

const routerUtils = useRouterUtils()

const onLoginClicked = async () => await routerUtils.softNavigateToPage(ECoreRouterPage.LOGIN)
const onOpenAppClicked = async () => {
  await routerUtils.softNavigateToPage(ECoreRouterPage.APP_HOME)
}

const onLogoutClicked = async () => {
  await useAuthenticationContext().logoutAndClear()
  await routerUtils.hardNavigateToPage(ECoreRouterPage.INDEX)
}

</script>

<style scoped>

.centered-row {
  margin-top: 5em;
  display: flex;
  justify-content: center;
  flex-wrap: wrap;
  gap: 1em;
}

.link-block {
  margin-top: 1em;
  display: flex;
  justify-content: flex-start;
  gap: 1.5em;
  flex-wrap: wrap;
}

.buttons {
  width: 20em;
}

.link-block-container {
  position: fixed;
  bottom: 8em;
}

.name-title {
  font-size: 30px;
  color: grey;
}

.name-title span{
  color: black;
}

</style>