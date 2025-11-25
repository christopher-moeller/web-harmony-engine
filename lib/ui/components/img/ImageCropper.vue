<template>
  <div class="image-resizer-container">
    <img ref="imageRef" :src="imageSrc" class="image" style="max-height: 400px" alt="" @load="onImgLoaded"/>
    <resizable-rectangle
        v-if="imageLoaded"
        v-model="computedModel"
        :initial-box-values="initialRectangleValues"
        :image-data="imageData"
    />
  </div>
</template>

<script setup lang="ts">
import {ref,PropType} from 'vue';
import {ImageCropperRectangle, ImageData, Rectangle} from "~/utils/HarmonyTypes";
import {ComputedUtils} from "~/utils/ComputedUtils";
import {WritableComputedRef} from "@vue/reactivity";
import ResizableRectangle from "~/components/img/ResizableRectangle.vue";

const props = defineProps({
  imageSrc: {
    type: String
  },
  modelValue: {
    type: Object as PropType<ImageCropperRectangle>,
    required: true
  }
})

const emit = defineEmits<{
  (e: 'update:modelValue'): void
}>()

const computedModel:WritableComputedRef<ImageCropperRectangle> = ComputedUtils.createDelegatedComputedValue(props, getCurrentInstance()!)

const imageRef = ref()


const initialRectangleValues = ref<Rectangle>({x: 0, y: 0, width: 0, height: 0})
const imageLoaded = ref(false)

const translateNaturalPxToDisplayPx = (naturalPx: number, naturalImageSize: number, displayImageSize: number):number => {
  return naturalPx * (displayImageSize / naturalImageSize)
}


const imageData = ref<ImageData>()

const onImgLoaded = () => {

  imageData.value = {
    naturalWidth: imageRef.value!.naturalWidth,
    naturalHeight: imageRef.value!.naturalHeight,
    displayWidth: imageRef.value!.width,
    displayHeight: imageRef.value!.height
  }


  initialRectangleValues.value.x = translateNaturalPxToDisplayPx(computedModel.value.topLeftX, imageData.value?.naturalWidth, imageData.value?.displayWidth)
  initialRectangleValues.value.y = translateNaturalPxToDisplayPx(computedModel.value.topLeftY, imageData.value?.naturalHeight, imageData.value?.displayHeight)
  initialRectangleValues.value.width = translateNaturalPxToDisplayPx(computedModel.value.bottomRightX, imageData.value?.naturalWidth, imageData.value?.displayWidth) - initialRectangleValues.value.x
  initialRectangleValues.value.height = translateNaturalPxToDisplayPx(computedModel.value.bottomRightY, imageData.value?.naturalHeight, imageData.value?.displayHeight) - initialRectangleValues.value.y

  imageLoaded.value = true
}


</script>

<style scoped>
.image-resizer-container {
  position: relative;
  display: inline-block;
  background-color: green;
}

.image {
  display: block;
  max-width: 100%;
  height: auto;
}



</style>
