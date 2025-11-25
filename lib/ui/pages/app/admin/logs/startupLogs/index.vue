<template>
  <GenericResourceOverview :columns="columns" resource-name="startupLogs" />
</template>

<script lang="ts" setup>

import GenericResourceOverview from "~/components/view/GenericResourceOverview.vue"
import I18N from "@core/utils/I18N";
import TextFieldFilter from "@core/components/base/utils/filter/TextFieldFilter.vue";
import HarmonyYesNoModal from "@core/components/base/HarmonyYesNoModal.vue";
import HarmonyThreeDotsMenuItem from "@core/components/base/HarmonyThreeDotsMenuItem.vue";
import useCoreApi from "~/composables/useCoreApi";
import {HarmonyTableColumn} from "~/utils/HarmonyTypes";

const i18n = I18N.of("startupLogs-index")

const columns:HarmonyTableColumn[] = [
  {
    id: "startedAt",
    caption: i18n.translate('Started At').build()
  },
  {
    id: "durationInMillis",
    caption: i18n.translate('Duration Milliseconds').build()
  },
  {
    id: "durationText",
    caption: i18n.translate('Duration').build()
  }
]


</script>

<style scoped>

</style>