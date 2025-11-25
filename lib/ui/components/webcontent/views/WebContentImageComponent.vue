<template>
  <template v-if="computedModel.fileWebData">
    <div v-if="isActive && context.isInEditMode" class="edit-header">
      <div class="size-panel">
        <input @input="onWidthInput" class="size-input" v-model="width" type="number"/>
        <input @input="onHeightInput" class="size-input" v-model="height" type="number"/>
        <div>
          <span style="margin-right: 1em">Show Title</span>
          <input type="checkbox" v-model="showImageTitle">
        </div>
      </div>
      <div>
        <i class='bx bx-x-circle remove-icon' @click="onRemoveClick"></i>
      </div>
    </div>
    <div style="display: flex; align-items: center; flex-direction: column">
      <img @load="onImgLoad" ref="img" :src="getImageSrc()" alt="img" :width="width" :height="height" class="img"/>
      <p v-if="showImageTitle" class="img-title" :contenteditable="context.isInEditMode" @input="onTextChanged($event)">{{computedModel.imageTitle}}</p>
    </div>
  </template>
  <template v-else>
    <HarmonyFileSelector :min="1" :max="1" :caption="i18n.translate('Image to upload').build()" v-model="imageList"/>
  </template>
</template>

<script setup lang="ts">

import {PropType, ref} from "vue";
import {FileWebData, ImageWebContent} from "~/CoreApi";
import {WritableComputedRef} from "@vue/reactivity";
import {ComputedUtils} from "~/utils/ComputedUtils";
import {WebContentContext} from "~/utils/HarmonyTypes";
import HarmonyFileSelector from "~/components/base/HarmonyFileSelector.vue";
import HarmonyCheckbox from "~/components/base/HarmonyCheckbox.vue";

const i18n = I18N.of("WebContentImageComponent")

const props = defineProps({
  modelValue: {
    type: Object as PropType<ImageWebContent>,
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

const img = ref()

const imageList = ref<FileWebData[]>([])
watch(imageList, files => {
  if(files.length === 1) {
    computedModel.value.fileWebData = files[0]
  }
}, {deep: true})


const computedModel:WritableComputedRef<ImageWebContent> = ComputedUtils.createDelegatedComputedValue(props, getCurrentInstance()!)

const width:WritableComputedRef<number> = computed({
  get: () => computedModel.value.width,
  set: (value) => computedModel.value.width = value
})

const height:WritableComputedRef<number> = computed({
  get: () => computedModel.value.height!,
  set: (value) => computedModel.value.height = value
})

const showImageTitle:WritableComputedRef<boolean> = computed({
  get: () => !!computedModel.value.showImageTitle,
  set: (value) => computedModel.value.showImageTitle = value
})

const dimCalculationFactor = ref()

const updateDimCalculationFactor = () => {
  dimCalculationFactor.value = width.value / height.value
}

const onWidthInput = () => {
  height.value = (width.value / dimCalculationFactor.value) | 0
}

const onHeightInput = () => {
  width.value = (height.value * dimCalculationFactor.value) | 0
}

const getImageSrc = ():string => {
  const webData:FileWebData = computedModel.value.fileWebData!
  if(webData.simpleDownloadLink) {
    return webData.simpleDownloadLink!.replaceAll("{webContentUUID}", props.context?.id!)
  } else if(webData.base64Content) {
    return webData.base64Content+""
  } else {
    return ""
  }
}

const onTextChanged = (event:any) => {
  computedModel.value.imageTitle = event.target.innerText
}

const onRemoveClick = () => {
  imageList.value = []
  computedModel.value.fileWebData = undefined
}

const onImgLoad = (event:any) => {
  if(width.value && height.value) {
    updateDimCalculationFactor()
    return
  }
  const eWidth = event.target.width;
  const eHeight = event.target.height
  if(eWidth > 0 && eHeight > 0) {
    width.value = eWidth
    height.value = eHeight
    updateDimCalculationFactor()
  }
}

</script>

<style scoped>

.edit-header {
  display: flex;
  justify-content: space-between;
  align-items: baseline;
  margin-bottom: 1em;
}

.size-panel {
  display: flex;
  gap: 1em
}

.size-input {
  width: 4em;
}

.remove-icon {
  color: var(--harmony-primary);
  cursor: pointer;
}

.img {

}

.img-title {
  color: var(--harmony-light-3);
}

</style>