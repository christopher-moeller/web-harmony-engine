<template>
  <HarmonyTable :columns="internalColumns" :rows="rows" :card-representation="cardRepresentation">
    <template #custom-action-header>
      <slot name="custom-action-header"></slot>
    </template>
    <template #header:checked>
      <HarmonyCheckBox v-if="!singleSelection" v-model="allSelected"/>
    </template>
    <template #column:checked="{ row }">
      <HarmonyCheckBox @update:modelValue="(v) => onCheckboxInput(row, v)" :model-value="getCheckBoxReadOnlyComputedValue(row).value"/>
    </template>
  </HarmonyTable>
</template>

<script lang="ts" setup>

import HarmonyTable from "@core/components/base/table/HarmonyTable.vue"
import {computed, PropType, ref, watch} from "vue";
import HarmonyCheckBox from "@core/components/base/HarmonyCheckbox.vue"
import {ComputedUtils} from "~/utils/ComputedUtils";
import {HarmonyTableColumn} from "~/utils/HarmonyTypes";

const props = defineProps({
  columns: {
    type: Array as PropType<HarmonyTableColumn[]>,
    required: true
  },
  rows: {
    type: Array as PropType<Object>,
    required: true
  },
  cardRepresentation: {
    type: Boolean,
    required: false,
    default: false
  },
  modelValue: {
    type: Array,
    required: true
  },
  singleSelection: {
    type: Boolean,
    required: false,
    default: false
  }
})

const internalColumns = computed(() => [...props.columns, {id: "checked", caption: "", fixedLeft: true}])
const computedModel = ComputedUtils.createDelegatedComputedValue(props, getCurrentInstance()!)

const getCheckBoxReadOnlyComputedValue = (row:Object) => computed(() => {
  // @ts-ignore
  if(row.primaryKey) {
    // @ts-ignore
    return !!computedModel.value.find(e => e.primaryKey === row.primaryKey)
    // @ts-ignore
  } else if(row.value) {
    // @ts-ignore
    return !!computedModel.value.find(e => e === row.value || e?.value === row.value)
  } else {
    const rowString = JSON.stringify(row)
    return !!computedModel.value.find((e:Object) => JSON.stringify(e) === rowString)
  }
})

const onCheckboxInput = (row: Object, value: boolean): void => {
  if(value) {
    if(props.singleSelection) {
      computedModel.value.splice(0, computedModel.value.length)
    }
    computedModel.value.push(row)
  } else {

    let foundEntry;
    // @ts-ignore
    if(row.primaryKey) {
      // @ts-ignore
      foundEntry = computedModel.value.find(e => e.primaryKey === row.primaryKey)
      // @ts-ignore
    } else if(row.value) {
      // @ts-ignore
      foundEntry = computedModel.value.find(e => e === row.value || e?.value === row.value)
    } else {
      const rowString = JSON.stringify(row)
      foundEntry = computedModel.value.find((e:Object) => JSON.stringify(e) === rowString)
    }

    if(foundEntry) {
      const index = computedModel.value.indexOf(foundEntry)
      if(index >= 0) {
        computedModel.value.splice(index, 1);
      }
    }
  }
}

const allSelected = ref(false)
watch(allSelected, v => {
  if(v) {
    //@ts-ignore
    computedModel.value = [...props.rows]
  } else {
    computedModel.value = []
  }
})


</script>

<style scoped>

</style>