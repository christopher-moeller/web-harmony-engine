<template>
  <GenericResourceOverview :columns="columns" resource-name="registryItems"/>
</template>

<script lang="ts" setup>

import GenericResourceOverview from "~/components/view/GenericResourceOverview.vue"
import I18N from "@core/utils/I18N";
import {HarmonyTableColumn} from "~/utils/HarmonyTypes";

const i18n = I18N.of("registryItems-index")

const columns:HarmonyTableColumn[] = [
  {
    id: "uniqueName",
    caption: i18n.translate("Unique Name").build(),
  },
  {
    id: "label",
    caption: i18n.translate("Label").build(),
  },
  {
    id: "description",
    caption: i18n.translate("Description").build(),
  }
]

</script>

<style scoped>

</style>