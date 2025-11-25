<template>
  <div v-if="context.isInEditMode && isActive">
    <client-only>
      <quill-editor v-model:content="valueModel" contentType="html"/>
    </client-only>
  </div>
  <div v-else class="root">
    <div v-html="computedModel.text" class="base-html-style"/>
  </div>
</template>

<script setup lang="ts">

import { QuillEditor } from '@vueup/vue-quill'
import '@vueup/vue-quill/dist/vue-quill.snow.css';

import {PropType} from "vue";
import {TextWebContent} from "~/CoreApi";
import {WritableComputedRef} from "@vue/reactivity";
import {ComputedUtils} from "~/utils/ComputedUtils";
import {WebContentContext} from "~/utils/HarmonyTypes";

const props = defineProps({
  modelValue: {
    type: Object as PropType<TextWebContent>,
    required: true
  },
  context: {
    type: Object as PropType<WebContentContext>,
    required: true
  },
  isActive: {
    type: Boolean,
    required: true
  }
})

const emit = defineEmits<{
  (e: 'update:modelValue'): void
}>()


const computedModel:WritableComputedRef<TextWebContent> = ComputedUtils.createDelegatedComputedValue(props, getCurrentInstance()!)

const valueModel = computed({
  get: () => computedModel.value.text,
  set: (v) => computedModel.value.text = v
})

const myTest = ref("Hallo")

</script>

<style>

.base-html-style li {
  list-style: unset !important;
}

.base-html-style {

}

.root {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin: 1em;
}

</style>