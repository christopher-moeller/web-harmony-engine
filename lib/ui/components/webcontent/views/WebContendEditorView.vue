<template>
  <harmony-icon-switch-button :options="viewOptions" v-model="viewOption" />
  <div class="root">
    <template v-if="viewOption === 'preview'">
      <web-content-component v-model="computedModel" :context="previewContext"/>
    </template>
    <template v-else-if="viewOption === 'edit'">
      <web-content-component v-model="computedModel" :context="editContext"/>
    </template>
    <template v-else-if="viewOption === 'split'">
      <harmony-split-screen>
        <template #left>
          <web-content-component v-model="computedModel" :context="previewContext"/>
        </template>
        <template #right>
          <web-content-component v-model="computedModel" :context="editContext"/>
        </template>
      </harmony-split-screen>
    </template>
    <template v-else-if="viewOption === 'code'">
      <harmony-json-editor v-model="computedModel"/>
    </template>
  </div>
</template>

<script setup lang="ts">

import WebContentComponent from "~/components/webcontent/views/WebContentComponent.vue";
import {SwitchButtonOption, WebContentContext} from "~/utils/HarmonyTypes";
import HarmonyIconSwitchButton from "~/components/base/HarmonySwitchButton.vue";
import HarmonySplitScreen from "~/components/base/HarmonySplitScreen.vue";
import HarmonyJsonEditor from "~/components/base/HarmonyJsonEditor.vue";
import {PropType} from "vue";
import {AbstractWebContent} from "~/CoreApi";
import {WritableComputedRef} from "@vue/reactivity";
import {ComputedUtils} from "~/utils/ComputedUtils";

const props = defineProps({
  modelValue: {
    type: Object as PropType<AbstractWebContent>,
    required: true
  },
  webContentId: {
    type: String,
    required: false
  }
})

const emit = defineEmits<{
  (e: 'update:modelValue'): void
}>()

const computedModel:WritableComputedRef<AbstractWebContent> = ComputedUtils.createDelegatedComputedValue(props, getCurrentInstance()!)

const coreApi = useCoreApi()

const previewContext = ref<WebContentContext>({
  isInEditMode: false,
  hoverComponentStack: [],
  id: props.webContentId
})

const editContext = ref<WebContentContext>({
  isInEditMode: true,
  hoverComponentStack: [],
  id: props.webContentId
})

const viewOptions:SwitchButtonOption[] = [
  {
    id: "preview",
    iCssClass: "bx bx-square"
  },
  {
    id: "edit",
    iCssClass: "bx bx-edit"
  },
  {
    id: "split",
    iCssClass: "bx bx-sidebar"
  },
  {
    id: "code",
    iCssClass: "bx bx-code-block"
  }
]

const viewOption = ref("preview")

</script>

<style scoped>

.split-screen {
  display: flex;
}

.root {
  margin-top: 1em;
  border: 3px solid var(--harmony-light-3);
  border-radius: 10px;
  background-color: var(--harmony-light-1);
  padding: 0.5em;
}

</style>