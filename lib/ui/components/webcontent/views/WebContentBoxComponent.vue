<template>
  <template v-if="context.isInEditMode && isActive">
    <div class="action-row">
      <div>
        <harmony-icon-switch-button :options="alignOptions" v-model="alignOption" />
      </div>
      <div>
        <i class='bx bx-plus-circle add-icon' @click="isAddModalVisible = true"></i>
      </div>
    </div>
    <div :class="computedModel.align === BoxAlignment.VERTICAL ? 'vertical-align-root' : 'horizontal-align-root'">
      <template v-for="(_, index) in computedModel.children" :key="index">
        <div
            class="child-element"
            :style="{ display:  computedModel.align === BoxAlignment.HORIZONTAL ? 'block': 'flex'}"
            :class="{ dragging: index === draggingIndex }"
            :draggable="true"
            @dragstart="onDragStart(index, $event)"
            @dragover.prevent="onDragOver(index)"
            @dragenter.prevent
            @dragend="onDragEnd(index)"
        >
          <div class="drag-handler-area" :style="{ display:  computedModel.align === BoxAlignment.HORIZONTAL ? 'flex': '', justifyContent: 'center'}"
          >
            <i class='bx bx-grid-vertical'></i>
          </div>
          <div class="content">
            <web-content-component v-model="computedModel.children![index]" :context="context"/>
          </div>
          <div class="delete-button-area" :style="{ display:  computedModel.align === BoxAlignment.HORIZONTAL ? 'flex': '', justifyContent: 'center'}">
            <i class='bx bxs-minus-circle' @click="onDeleteChildrenClick(index)"></i>
          </div>
        </div>
      </template>
    </div>
    <box-add-new-component-modal v-model="isAddModalVisible" @on-add-new-component="onAddNewComponentSelected"/>
    <harmony-yes-no-modal
        v-model="showDeleteChildrenModal"
        :message="i18n.translate('Do you want to delete this component?').build()"
        @onYes="deleteChildrenComponent"
    />
  </template>
  <template v-else>
    <div :class="computedModel.align === BoxAlignment.VERTICAL ? 'vertical-align-root' : 'horizontal-align-root'">
      <template v-for="(_, index) in computedModel.children" :key="index">
        <web-content-component v-model="computedModel.children![index]" :context="context"/>
      </template>
    </div>
  </template>
</template>

<script setup lang="ts">

import {PropType} from "vue";
import {BoxAlignment, BoxWebContent} from "~/CoreApi";
import {WritableComputedRef} from "@vue/reactivity";
import {ComputedUtils} from "~/utils/ComputedUtils";
import WebContentComponent from "~/components/webcontent/views/WebContentComponent.vue";
import {SwitchButtonOption, WebContentComponentType, WebContentContext} from "~/utils/HarmonyTypes";
import BoxAddNewComponentModal from "~/components/webcontent/views/box/BoxAddNewComponentModal.vue";
import HarmonyYesNoModal from "~/components/base/HarmonyYesNoModal.vue";
import HarmonyIconSwitchButton from "~/components/base/HarmonySwitchButton.vue";

const i18n = I18N.of("WebContentBoxComponent")

const props = defineProps({
  modelValue: {
    type: Object as PropType<BoxWebContent>,
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


const computedModel:WritableComputedRef<BoxWebContent> = ComputedUtils.createDelegatedComputedValue(props, getCurrentInstance()!)

const draggingIndex = ref()
const dragOverIndex = ref()

const onDragStart = (index, event) => {
  draggingIndex.value = index;
}

const onDragOver = (index) => {
  if (dragOverIndex.value !== index && draggingIndex.value !== null) {

    const children = [...computedModel.value.children]
    const draggedItem = children[draggingIndex.value];
    children.splice(draggingIndex.value, 1);
    children.splice(index, 0, draggedItem);
    computedModel.value.children = children

    dragOverIndex.value = index;
    draggingIndex.value = index;
  }
}

const onDragEnd = (index) => {
  draggingIndex.value = null;
  dragOverIndex.value = null;
}

const isAddModalVisible = ref(false)
const showDeleteChildrenModal = ref(false)

const indexOfComponentToDelete = ref()
const onDeleteChildrenClick = (index:number) => {
  showDeleteChildrenModal.value = true
  indexOfComponentToDelete.value = index
}
const deleteChildrenComponent = () => {
  computedModel.value.children.splice(indexOfComponentToDelete.value, 1);
  indexOfComponentToDelete.value = undefined
}

const alignOptions:SwitchButtonOption[] = [
  {
    id: "VERTICAL",
    iCssClass: "bx bx-expand-vertical"
  },
  {
    id: "HORIZONTAL",
    iCssClass: "bx bx-expand-horizontal"
  }
]

const alignOption = computed({
  get: () => {
    return computedModel.value.align
  },
  set: (value) => {
    computedModel.value.align = value
  }
})

const onAddNewComponentSelected = (componentType:WebContentComponentType) => {
  const newInstance = componentType.createNewInstance()
  computedModel.value.children?.push(newInstance)
}

</script>

<style scoped>

.child-element {
  width: 100%;
  display: flex;
  transition: transform 0.2s ease, opacity 0.2s ease;
}

.child-element.dragging {
  opacity: 0.5;
}

.child-element:not(.dragging) {
  transition: transform 0.2s ease, opacity 0.2s ease;
}

.content {
  width: 100%;
  border: 2px solid var(--harmony-light-3);
  border-radius: 10px;
  margin: 0.5em;
}

.drag-handler-area {
  cursor: move;
  display: flex;
  align-items: center;
}

.delete-button-area {
  cursor: pointer;
  display: flex;
  align-items: center;
}

.action-row {
  display: flex;
  justify-content: space-between;
  margin-bottom: 1em;
}

.add-icon {
  color: var(--harmony-primary);
  cursor: pointer;
}

.vertical-align-root {

}

.horizontal-align-root {
  display: flex;
  justify-content: stretch;
  gap: 5px
}

</style>