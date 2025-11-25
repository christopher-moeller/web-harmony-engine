<template>
  <div class="content">
    <HarmonyContentSwitcher :steps="steps" v-model="activeStep">
      <template #account>
        <UserAccountGeneralStettingsStep
            @onChangePasswordClick="onChangePasswordClick"
            @on-delete-account-click="onDeleteAccountClick"
        />
      </template>
      <template #changePassword>
        <UserAccountPasswordStettingsStep
            @onBackToGeneral="onBackToGeneral"
        />
      </template>
      <template #deleteAccount>
        <UserAccountDeleteAccountSettingsStep
            @onBackToGeneral="onBackToGeneral"
        />
      </template>
    </HarmonyContentSwitcher>
  </div>
</template>

<script setup lang="ts">

import HarmonyContentSwitcher from "~/components/utils/HarmonyContentSwitcher.vue";
import {HarmonyContentSwitcherType} from "~/utils/HarmonyTypes";
import UserAccountGeneralStettingsStep
  from "~/components/form/viewmodel/useraccount/UserAccountGeneralSettingsStep.vue";
import UserAccountPasswordStettingsStep
  from "~/components/form/viewmodel/useraccount/UserAccountPasswordSettingsStep.vue";
import UserAccountDeleteAccountSettingsStep
  from "~/components/form/viewmodel/useraccount/UserAccountDeleteAccountSettingsStep.vue";

const steps:HarmonyContentSwitcherType[] = [
  {
    id: "account"
  },
  {
    id: "changePassword"
  },
  {
    id: "deleteAccount"
  }
]

const activeStep = ref("account")

const onChangePasswordClick = () => activeStep.value = "changePassword"
const onBackToGeneral = () => activeStep.value = "account"

const onDeleteAccountClick = () => activeStep.value = "deleteAccount"

</script>

<style scoped>

.content {
  display: flex;
  justify-content: center;
}

</style>