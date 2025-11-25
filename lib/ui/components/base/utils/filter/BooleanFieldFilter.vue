<template>
  <harmony-checkbox :caption="caption" v-model="value"/>
</template>

<script lang="ts" setup>
import {PropType, ref, watch} from "vue";
import {TableFilterContext} from "~/utils/HarmonyTypes";
import HarmonyCheckbox from "~/components/base/HarmonyCheckbox.vue";

const props = defineProps({
  filterKey: {
    type: String,
    required: true
  },
  caption: {
    type: String,
    required: true
  },
  tableFilterContext: {
    type: Object as PropType<TableFilterContext>,
    required: true
  }
})

const value = ref(props.tableFilterContext.activeFilters.find(e => e.key === props.filterKey)?.value)
watch(value, () => props.tableFilterContext.onValueChanged(props.filterKey, value.value))

</script>

<style scoped>

</style>