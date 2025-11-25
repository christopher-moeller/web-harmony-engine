<template>
  <GenericResourceOverview :columns="columns" resource-name="notifications">
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

const i18n = I18N.of("notifications-index")

const columns:HarmonyTableColumn[] = [
  {
    id: "sendAt",
    caption: i18n.translate('Send at').build()
  },
  {
    id: "recipient",
    caption: i18n.translate('Recipient').build()
  },
  {
    id: "caption",
    caption: i18n.translate('Caption').build()
  },
  {
    id: "read",
    caption: i18n.translate('Read').build()
  },
  {
    id: "type",
    caption: i18n.translate('Type').build()
  }
]

</script>

<style scoped>

</style>