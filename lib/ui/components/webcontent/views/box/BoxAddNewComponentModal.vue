<template>
  <harmony-modal
      v-model="computedModel"
      :is-minimal="true"
  >
    <div class="list-wrapper">
      <template v-for="(componentType, index) in componentTypes" :key="index">
        <harmony-panel style="margin: 1em" @click="onAddNewComponentClick(componentType)">
          <div class="panel-item">
            <i class='bx' :class="componentType.iconId" ></i>
            <p style="margin-left: 2em">{{componentType.getCaption()}}</p>
          </div>
        </harmony-panel>
      </template>
    </div>
  </harmony-modal>
</template>

<script setup lang="ts">

import {ComputedUtils} from "~/utils/ComputedUtils";
import HarmonyModal from "~/components/base/HarmonyModal.vue";
import {WebContentComponentType} from "~/utils/HarmonyTypes";
import WebContentUtils from "~/utils/WebContentUtils";
import HarmonyPanel from "~/components/base/HarmonyPanel.vue";

const i18n = I18N.of("BoxAddNewComponentModal")

const emit = defineEmits<{
  (e: 'update:modelValue'): void
  (e: 'onAddNewComponent', type:WebContentComponentType): void
}>()

const props = defineProps({
  modelValue: {
    type: Boolean,
    required: true
  }
})

const computedModel = ComputedUtils.createDelegatedComputedValue(props, getCurrentInstance()!)

const componentTypes:WebContentComponentType[] = WebContentUtils.getComponentTypes()

const onAddNewComponentClick = (componentType:WebContentComponentType) => {
  emit('onAddNewComponent', componentType)
  computedModel.value = false
}

</script>

<style scoped>

.list-wrapper {

}

.panel-item {
  display: flex;
  cursor: pointer;
}


</style>