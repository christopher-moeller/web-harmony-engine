<template>
  <GenericResourceOverview :columns="columns" resource-name="cronJobs"/>
</template>

<script lang="ts" setup>

import GenericResourceOverview from "~/components/view/GenericResourceOverview.vue"
import I18N from "@core/utils/I18N";
import {HarmonyTableColumn} from "~/utils/HarmonyTypes";

const i18n = I18N.of("cronJobs-index")

const columns:HarmonyTableColumn[] = [
  {
    id: "label",
    caption: i18n.translate("Label").build(),
  },
  {
    id: "description",
    caption: i18n.translate("Description").build(),
  },
  {
    id: "cronTrigger",
    caption: i18n.translate("Cron Trigger").build()
  }
]

</script>

<style scoped>

</style>