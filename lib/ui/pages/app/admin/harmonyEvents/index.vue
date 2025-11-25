<template>
  <GenericResourceOverview :columns="columns" resource-name="harmonyEvents">
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

const i18n = I18N.of("harmonyEvents-index")

const columns:HarmonyTableColumn[] = [
  {
    id: "createdAt",
    caption: i18n.translate('Created At').build()
  },
  {
    id: "javaType",
    caption: i18n.translate('Java Type').build()
  },
  {
    id: "createdBy",
    caption: i18n.translate('Created By').build(),
  }
]

</script>

<style scoped>

</style>