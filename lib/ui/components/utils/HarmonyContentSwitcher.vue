<template>
  <client-only>
    <template v-for="step in steps">
      <slot v-if="step.id === modelValue" :name="step.id" />
    </template>
  </client-only>
</template>

<script setup lang="ts">

import {PropType} from "vue";
import {HarmonyContentSwitcherType} from "~/utils/HarmonyTypes";
import {ComputedUtils} from "~/utils/ComputedUtils";

const props = defineProps({
  steps: {
    type: Array as PropType<HarmonyContentSwitcherType[]>,
    required: true
  },
  modelValue: {
    required: false
  }
})

const modelValue = ComputedUtils.createDelegatedComputedValue(props, getCurrentInstance()!)

</script>

<style scoped>

</style>