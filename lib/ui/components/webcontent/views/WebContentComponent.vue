<template>
  <div ref="component"
       :class="dynamicClass"
       @click="onComponentClick"
       @mouseenter.stop="onMouseEnter"
       @mouseleave.stop="onMouseLeave"
  >
    <template v-if="computedModel.type === 'title'">
      <p v-show="!showBehindGlass && context.isInEditMode" class="component-name">{{getComponentCaptionById('title')}}</p>
      <harmony-glass-door :showBehindGlass="showBehindGlass">
        <web-content-title-component v-model="computedModel" :context="context" />
      </harmony-glass-door>
    </template>
    <template v-else-if="computedModel.type === 'text'">
      <p v-show="!showBehindGlass && context.isInEditMode" class="component-name">{{getComponentCaptionById('text')}}</p>
      <harmony-glass-door :showBehindGlass="showBehindGlass">
        <web-content-text-component v-model="computedModel" :context="context" :is-active="state === 'ACTIVE'"/>
      </harmony-glass-door>
    </template>
    <template v-else-if="computedModel.type === 'image'">
      <p v-show="!showBehindGlass && context.isInEditMode" class="component-name">{{getComponentCaptionById('image')}}</p>
      <harmony-glass-door :showBehindGlass="showBehindGlass">
        <web-content-image-component v-model="computedModel" :context="context" :is-active="state === 'ACTIVE'"/>
      </harmony-glass-door>
    </template>
    <template v-else-if="computedModel.type === 'box'">
      <p v-show="!showBehindGlass && context.isInEditMode" class="component-name">{{getComponentCaptionById('box')}}</p>
      <web-content-box-component v-model="computedModel" :context="context" :is-active="state === 'ACTIVE'"/>
    </template>
  </div>
</template>

<script setup lang="ts">

import {computed, PropType} from "vue";
import {AbstractWebContent} from "~/CoreApi";
import {WritableComputedRef} from "@vue/reactivity";
import {ComputedUtils} from "~/utils/ComputedUtils";
import WebContentTitleComponent from "~/components/webcontent/views/WebContentTitleComponent.vue";
import WebContentTextComponent from "~/components/webcontent/views/WebContentTextComponent.vue";
import WebContentBoxComponent from "~/components/webcontent/views/WebContentBoxComponent.vue";
import {WebContentContext} from "~/utils/HarmonyTypes";
import HarmonyGlassDoor from "~/components/base/HarmonyGlassDoor.vue";
import WebContentUtils from "~/utils/WebContentUtils";
import WebContentImageComponent from "~/components/webcontent/views/WebContentImageComponent.vue";

const props = defineProps({
  modelValue: {
    type: Object as PropType<AbstractWebContent>,
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

const component = ref()

const componentId:number = getCurrentInstance()!.uid

const state = computed(() => {
  if(props.context?.activeComponentId) {
    if(props.context?.activeComponentId === componentId) {
      return "ACTIVE"
    } else {
      return "INACTIVE"
    }
  } else {
    return "DEFAULT"
  }
})

const showBehindGlass = computed(() => state.value === "INACTIVE" && props.context?.isInEditMode)

const isHover = computed(() => {
  const stack = props.context?.hoverComponentStack
  return stack ? stack[stack.length - 1] === componentId : false
})

const dynamicClass = computed(() => {
  if(!props.context?.isInEditMode) {
    return []
  }
  if(state.value === "ACTIVE") {
    return ["component", "active-style"]
  } else if(state.value === "INACTIVE") {
    return ["component", "inactive-style"]
  } else if(state.value === "DEFAULT") {
    const classes =  ["component", "default-style"]
    if(isHover.value) {
      classes.push("hover")
    }
    return classes
  }
})

const computedModel:WritableComputedRef<AbstractWebContent> = ComputedUtils.createDelegatedComputedValue(props, getCurrentInstance()!)

const onComponentClick = (event:any) => {
  if (event) {
    event.stopPropagation()
  }
  if(props.context?.activeComponentId !== componentId) {
    if(props.context?.activeComponentId) {
      props.context!.activeComponentId = undefined
    } else {
      props.context!.activeComponentId = componentId
    }
  }
}

const onMouseEnter = () => {
  props.context?.hoverComponentStack.push(componentId)
}

const onMouseLeave = () => {
  props.context?.hoverComponentStack.pop()
}

const onOutsideClick = () => {
  props.context!.activeComponentId = undefined
}

const handleClickOutside = (event:any) => {
  if(event) {
    event.stopPropagation()
  }
  // Check if the click happened exactly on the div element itself
  if (component.value && event.target !== component.value) {
    onOutsideClick();
  }
}

onMounted(() => {
  document.addEventListener("click", handleClickOutside);
})

onBeforeUnmount(() => {
  document.removeEventListener("click", handleClickOutside);

})

const getComponentCaptionById = (id: string):string => {
  return WebContentUtils.getComponentTypeById(id).getCaption()
}

</script>

<style scoped>

.component {
  border: 2px solid var(--harmony-light-3);
  border-radius: 10px;
  margin: 1em;
  padding: 0.5em;
  width: 90%;
  min-width: fit-content;
}

.active-style {
  border-color: var(--harmony-primary);
}

.inactive-style {
  border-color: transparent;
}

.default-style {
  cursor: pointer;
}

.hover {
  border-color: var(--harmony-primary);
}

.component-name {
  margin-top: -1.25em;
  background-color: var(--harmony-light-1);
  width: fit-content;
  padding: 0 0.5em;
}


</style>