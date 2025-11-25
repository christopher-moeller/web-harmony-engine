<template>
  <GenericResourceOverview :columns="columns" resource-name="actors">
    <template #filter="{filterContext}">
      <text-field-filter filter-key="uniqueName" :caption="i18n.translate('Unique Name').build()" :table-filter-context="filterContext"/>
    </template>
  </GenericResourceOverview>
</template>

<script lang="ts" setup>

import GenericResourceOverview from "~/components/view/GenericResourceOverview.vue"
import TextFieldFilter from "@core/components/base/utils/filter/TextFieldFilter.vue";
import I18N from "@core/utils/I18N";
import {HarmonyTableColumn} from "~/utils/HarmonyTypes";

const i18n = I18N.of("actors-index")

const columns:HarmonyTableColumn[] = [
  {
    id: "uniqueName",
    caption: i18n.translate('Unique Name').build()
  },
  {
    id: "type",
    caption: i18n.translate('Type').build()
  },
  {
    id: "userId",
    caption: i18n.translate('User ID').build()
  }
]

</script>

<style scoped>

</style>