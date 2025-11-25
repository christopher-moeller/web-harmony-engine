<template>
  <GenericResourceOverview :columns="columns" initial-sort-by-column="sendAt:desc" resource-name="personalNotifications">
    <template #filter="{filterContext}">
      <text-field-filter filter-key="search" :caption="i18n.translate('Search').build()" :table-filter-context="filterContext"/>
    </template>

    <template v-slot:column="{cell, row}">
      <template v-if="isNotificationRead(row)">
        <p>{{cell}}</p>
      </template>
      <template v-else>
        <p><b>{{cell}}</b></p>
      </template>
    </template>
  </GenericResourceOverview>
</template>

<script lang="ts" setup>

import GenericResourceOverview from "~/components/view/GenericResourceOverview.vue"
import TextFieldFilter from "@core/components/base/utils/filter/TextFieldFilter.vue";
import I18N from "@core/utils/I18N";
import {HarmonyTableColumn} from "~/utils/HarmonyTypes";

const i18n = I18N.of("personalNotifications-index")

const columns:HarmonyTableColumn[] = [
  {
    id: "sendAt",
    caption: i18n.translate('Send at').build()
  },
  {
    id: "caption",
    caption: i18n.translate('Caption').build()
  },
  {
    id: "type",
    caption: i18n.translate('Type').build()
  }
]

const isNotificationRead = (row: any): boolean => !!row.data?.read

</script>

<style scoped>

</style>