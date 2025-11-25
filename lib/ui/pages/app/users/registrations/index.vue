<template>
  <GenericResourceOverview
      :columns="columns"
      custom-resource-name="userRegistrations"
      resource-name="userRegistrations"
  >
    <template v-slot:actionArea>
      <harmony-button
          :caption="i18n.translate('Send Invitation').build()"
          @click="onSendInvitationClick"
          :disabled="!isInvitationAllowed"
      />
    </template>
  </GenericResourceOverview>
</template>

<script lang="ts" setup>

import GenericResourceOverview from "~/components/view/GenericResourceOverview.vue"
import HarmonyButton from "@core/components/base/HarmonyButton.vue";
import I18N from "@core/utils/I18N";
import {ref} from "vue";
import useRouterUtils from "~/composables/useRouterUtils";
import useCoreApi from "~/composables/useCoreApi";
import {HarmonyTableColumn} from "~/utils/HarmonyTypes";

const i18n = I18N.of("registrations-index")

const coreApi = useCoreApi()
const configurationResponse = await coreApi.api().getUserRegistrationApi().getConfiguration()
const isInvitationAllowed = ref(configurationResponse.data.isInvitationAllowed)

const columns:HarmonyTableColumn[] = [
  {
    id: "email",
    caption: i18n.translate('E-Mail').build()
  },
  {
    id: "state",
    caption: i18n.translate('State').build()
  }
]

const routerUtils = useRouterUtils()
const currentPath = routerUtils.getCurrentRoute().value.path
const onSendInvitationClick = () => routerUtils.softNavigateToPath(currentPath + "/" + "sendInvitation")

</script>

<style scoped>

</style>