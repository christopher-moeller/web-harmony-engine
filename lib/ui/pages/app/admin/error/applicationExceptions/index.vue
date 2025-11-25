<template>
  <GenericResourceOverview :columns="columns" ref="overviewContainer" resource-name="applicationExceptions">
    <template #filter="{filterContext}">
      <text-field-filter filter-key="search" :caption="i18n.translate('Search').build()" :table-filter-context="filterContext"/>
    </template>
    <template #menuItems>
      <harmony-three-dots-menu-item :caption="i18n.translate('Delete all').build()" @click="onDeleteAllClick"/>
    </template>
  </GenericResourceOverview>
  <HarmonyYesNoModal
      v-model="showShouldDeleteQuestionModal"
      :message="i18n.translate('Do you want to delete all exceptions?').build()"
      @onYes="deleteAllExceptions"
  />
</template>

<script lang="ts" setup>

import GenericResourceOverview from "~/components/view/GenericResourceOverview.vue"
import I18N from "@core/utils/I18N";
import TextFieldFilter from "@core/components/base/utils/filter/TextFieldFilter.vue";
import HarmonyYesNoModal from "@core/components/base/HarmonyYesNoModal.vue";
import HarmonyThreeDotsMenuItem from "@core/components/base/HarmonyThreeDotsMenuItem.vue";
import useCoreApi from "~/composables/useCoreApi";
import {HarmonyTableColumn} from "~/utils/HarmonyTypes";

const i18n = I18N.of("applicationExceptions-index")

const overviewContainer = ref()

const columns:HarmonyTableColumn[] = [
  {
    id: "timestamp",
    caption: i18n.translate('Timestamp').build()
  },
  {
    id: "exceptionType",
    caption: i18n.translate('Exception Type').build()
  },
  {
    id: "codeLocation",
    caption: i18n.translate('Code Location').build()
  },
  {
    id: "message",
    caption: i18n.translate('Message').build()
  }
]

const showShouldDeleteQuestionModal = ref(false)

const onDeleteAllClick = () => {
  showShouldDeleteQuestionModal.value = true
}

const coreApi = useCoreApi()
const deleteAllExceptions = async () => {
  await coreApi.api().getApplicationExceptionApi().deleteAllExceptions()
  overviewContainer.value.fetchCleanNewData()
}

</script>

<style scoped>

</style>