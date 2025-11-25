<template>
  <div class="root">
    <h3 :contenteditable="context.isInEditMode" @input="onTextChanged($event)">{{computedModel.title}}</h3>
  </div>
</template>

<script setup lang="ts">

import {PropType} from "vue";
import {TitleWebContent} from "~/CoreApi";
import {WritableComputedRef} from "@vue/reactivity";
import {ComputedUtils} from "~/utils/ComputedUtils";
import {WebContentContext} from "~/utils/HarmonyTypes";

const props = defineProps({
  modelValue: {
    type: Object as PropType<TitleWebContent>,
    required: true
  },
  context: {
    type: Object as PropType<WebContentContext>,
    required: true
  }
})

const emit = defineEmits<{
  (e: 'update:modelValue'): void
}>()


const computedModel:WritableComputedRef<TitleWebContent> = ComputedUtils.createDelegatedComputedValue(props, getCurrentInstance()!)


const onTextChanged = (event:any) => {
  const text = event.target.innerText
  computedModel.value.title = text
}

</script>

<style scoped>

.root {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin: 1em;
}

</style>