<template>
  <button v-for="(option, index) in options" :key="index" class="btn"
          :class="computedModel === option.id ? 'btn-active' : ''"
          @click="onOptionClick(option)"
  >
    <i :class="option.iCssClass" class="bx-sm"></i>
  </button>
</template>

<script lang="ts" setup>


import {PropType} from "vue";
import {ComputedUtils} from "~/utils/ComputedUtils";
import {SwitchButtonOption} from "~/utils/HarmonyTypes";

const props = defineProps({
  options: {
    type: Array as PropType<SwitchButtonOption[]>,
    required: true
  },
  modelValue: {
    type: String,
    required: false
  }
})

const computedModel = ComputedUtils.createDelegatedComputedValue(props, getCurrentInstance()!)

const onOptionClick = (option: SwitchButtonOption): void => {
  computedModel.value = option.id
}


</script>

<style scoped>
.btn {
  background-color: var(--harmony-light-1);
  color: var(--harmony-light-3);
  border: none;
  padding: 6px 8px;
  cursor: pointer;
  border-radius: 5px;
  margin: 2px;
}

.btn:hover {
  background-color: var(--harmony-light-3);
  color: var(--harmony-light-1);
}

.btn-active {
  background-color: var(--harmony-light-3);
  color: var(--harmony-light-1);
}
</style>