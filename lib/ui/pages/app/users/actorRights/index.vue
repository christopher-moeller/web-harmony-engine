<template>
  <GenericResourceOverview :columns="columns" resource-name="actorRights">
    <template #filter="{filterContext}">
      <text-field-filter filter-key="search" :caption="i18n.translate('Search').build()" :table-filter-context="filterContext"/>
    </template>
  </GenericResourceOverview>
</template>

<script lang="ts" setup>

import GenericResourceOverview from "~/components/view/GenericResourceOverview.vue"
import TextFieldFilter from "@core/components/base/utils/filter/TextFieldFilter.vue";
import I18N from "@core/utils/I18N";
import {HarmonyTableColumn} from "~/utils/HarmonyTypes";

const i18n = I18N.of("actorRights-index")

const columns:HarmonyTableColumn[] = [
  {
    id: "label",
    caption: i18n.translate('Label').build()
  },
  {
    id: "description",
    caption: i18n.translate('Description').build()
  }
]

</script>

<style scoped>

</style>