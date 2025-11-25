<template>
  <GenericResourceOverview :columns="columns" resource-name="webContents"/>
</template>

<script lang="ts" setup>

import GenericResourceOverview from "~/components/view/GenericResourceOverview.vue"
import I18N from "@core/utils/I18N";
import {HarmonyTableColumn} from "~/utils/HarmonyTypes";

const i18n = I18N.of("webContents-index")

const columns:HarmonyTableColumn[] = [
  {
    id: "label",
    caption: i18n.translate("Label").build(),
  },
  {
    id: "area",
    caption: i18n.translate("Area").build(),
  }
]

</script>

<style scoped>

</style>