<template>
  <GenericResourceOverview :columns="columns" resource-name="serverTasks"/>
</template>

<script lang="ts" setup>

import GenericResourceOverview from "~/components/view/GenericResourceOverview.vue"
import I18N from "~/utils/I18N";
import {HarmonyTableColumn} from "~/utils/HarmonyTypes";

const i18n = I18N.of("serverTasks-index")

const columns:HarmonyTableColumn[] = [
  {
    id: "taskId",
    caption: i18n.translate("ID").build(),
  },
  {
    id: "taskName",
    caption: i18n.translate("Name").build(),
  }
]

</script>

<style scoped>

</style>