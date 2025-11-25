<template>
  <div class="image-resizer-container">
    <img ref="imageRef" :src="imageSrc" class="image" style="max-height: 400px" alt="" @load="onImgLoaded"/>
    <i class='bx bx-plus-circle add-rectangle-btn' @click="onAddNewRectangleClick"></i>
    <template v-for="(_, index) in computedModel.rectangles" :key="index">
      <resizable-rectangle
          v-if="imageLoaded"
          v-model="computedModel.rectangles![index]"
          :initial-box-values="initialRectangleValues"
          :image-data="imageData"
      />
    </template>
    <!--
    <resizable-rectangle
        v-if="imageLoaded"
        v-model="computedModel"
        :initial-box-values="initialRectangleValues"
        :image-data="imageData"
    />
    -->
  </div>
</template>

<script setup lang="ts">
import {ref,PropType} from 'vue';
import {ImageCropperRectangle, ImageData, ImageMultiRectangleData, Rectangle} from "~/utils/HarmonyTypes";
import {ComputedUtils} from "~/utils/ComputedUtils";
import {WritableComputedRef} from "@vue/reactivity";
import HarmonyButton from "@core/components/base/HarmonyButton.vue";
import ResizableRectangle from "~/components/img/ResizableRectangle.vue";

const props = defineProps({
  imageSrc: {
    type: String
  },
  modelValue: {
    type: Object as PropType<ImageMultiRectangleData>,
    required: true
  }
})

const emit = defineEmits<{
  (e: 'update:modelValue'): void
}>()

const computedModel:WritableComputedRef<ImageMultiRectangleData> = ComputedUtils.createDelegatedComputedValue(props, getCurrentInstance()!)

const imageRef = ref()


const initialRectangleValues = ref<Rectangle>({x: 0, y: 0, width: 0, height: 0})
const imageLoaded = ref(false)

const translateNaturalPxToDisplayPx = (naturalPx: number, naturalImageSize: number, displayImageSize: number):number => {
  return naturalPx * (displayImageSize / naturalImageSize)
}

const onAddNewRectangleClick = () => {
  computedModel.value.rectangles.push({
    topLeftX: 0,
    topLeftY: 0,
    bottomRightX: 0,
    bottomRightY: 0
  })
}

const imageData = ref<ImageData>()

const onImgLoaded = () => {

  imageData.value = {
    naturalWidth: imageRef.value!.naturalWidth,
    naturalHeight: imageRef.value!.naturalHeight,
    displayWidth: imageRef.value!.width,
    displayHeight: imageRef.value!.height
  }

  imageLoaded.value = true
}


</script>

<style scoped>
.image-resizer-container {
  position: relative;
  display: inline-block;
}

.image {
  display: block;
  max-width: 100%;
  height: auto;
}

.add-rectangle-btn {
  position: absolute;
  top: 0;
  right: 0;
  z-index: 10;
  cursor: pointer;
  color: var(--harmony-primary);
}



</style>
