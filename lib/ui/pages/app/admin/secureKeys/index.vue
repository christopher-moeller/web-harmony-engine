<template>
  <GenericResourceOverview :columns="columns" ref="overview" resource-name="secureKeys"/>
</template>

<script lang="ts" setup>

import GenericResourceOverview from "~/components/view/GenericResourceOverview.vue"
import I18N from "@core/utils/I18N";
import {ref} from "vue";
import {HarmonyTableColumn} from "~/utils/HarmonyTypes";

const overview = ref()

const i18n = I18N.of("secureKeys-index")

const columns:HarmonyTableColumn[] = [
  {
    id: "id",
    caption: i18n.translate('ID').build()
  },
  {
    id: "name",
    caption: i18n.translate('Name').build(),
  },
  {
    id: "key",
    caption: i18n.translate('Key').build()
  }
]

</script>

<style scoped>

</style>