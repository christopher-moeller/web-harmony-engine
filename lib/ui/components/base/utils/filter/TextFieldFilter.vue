<template>
  <harmony-text-field :caption="caption" v-model="value"/>
</template>

<script lang="ts" setup>
import {ref, watch} from "vue";
import {PropType} from "vue";
import FunctionUtils from "@core/utils/FunctionUtils";
import {TableFilterContext} from "~/utils/HarmonyTypes";
import HarmonyTextField from "~/components/base/HarmonyTextField.vue";


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
const debounceFunction = FunctionUtils.debounce((filterKey: string, value: string) => props.tableFilterContext.onValueChanged(filterKey, value))
watch(value, v => debounceFunction(props.filterKey, v!))

</script>

<style scoped>

</style>