<template>
  <GenericResourceOverview :columns="columns" resource-name="emails">
    <template #filter="{filterContext}">
      <text-field-filter filter-key="search" :caption="i18n.translate('Search').build()" :table-filter-context="filterContext"/>
    </template>
  </GenericResourceOverview>
</template>

<script lang="ts" setup>

import GenericResourceOverview from "~/components/view/GenericResourceOverview.vue"
import I18N from "@core/utils/I18N";
import TextFieldFilter from "~/components/base/utils/filter/TextFieldFilter.vue";
import {HarmonyTableColumn} from "~/utils/HarmonyTypes";

const i18n = I18N.of("emails-index")

const columns:HarmonyTableColumn[] = [
  {
    id: "createdAt",
    caption: i18n.translate('Created At').build()
  },
  {
    id: "lastSending",
    caption: i18n.translate('Last Send At').build()
  },
  {
    id: "state",
    caption: i18n.translate('State').build(),
  },
  {
    id: "toEmail",
    caption: i18n.translate('To').build()
  },
  {
    id: "subject",
    caption: i18n.translate('Subject').build()
  }
]

</script>

<style scoped>

</style>