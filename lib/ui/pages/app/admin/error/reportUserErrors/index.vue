<template>
  <GenericResourceOverview :columns="columns" resource-name="reportUserErrors">
    <template #filter="{filterContext}">
      <text-field-filter filter-key="search" :caption="i18n.translate('Search').build()" :table-filter-context="filterContext"/>
    </template>
  </GenericResourceOverview>
</template>

<script lang="ts" setup>

import GenericResourceOverview from "~/components/view/GenericResourceOverview.vue"
import I18N from "@core/utils/I18N";
import TextFieldFilter from "@core/components/base/utils/filter/TextFieldFilter.vue";
import {HarmonyTableColumn} from "~/utils/HarmonyTypes";

const i18n = I18N.of("reportUserErrors-index")

const columns:HarmonyTableColumn[] = [
  {
    id: "page",
    caption: i18n.translate('Page').build()
  },
  {
    id: "description",
    caption: i18n.translate('Description').build()
  }
]

</script>

<style scoped>

</style>