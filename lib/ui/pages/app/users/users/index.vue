<template>
  <GenericResourceOverview :columns="columns" resource-name="users"></GenericResourceOverview>
</template>

<script lang="ts" setup>

import GenericResourceOverview from "~/components/view/GenericResourceOverview.vue"
import I18N from "@core/utils/I18N";
import {HarmonyTableColumn} from "~/utils/HarmonyTypes";

const i18n = I18N.of("users-index")

const columns:HarmonyTableColumn[] = [
  {
    id: "email",
    caption: i18n.translate('E-Mail').build()
  },
  {
    id: "firstname",
    caption: i18n.translate('Firstname').build()
  },
  {
    id: "lastname",
    caption: i18n.translate('Lastname').build()
  }
]

</script>

<style scoped>

</style>