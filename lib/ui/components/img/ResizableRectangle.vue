<template>
  <div
      class="cropper-box"
      :style="cropperBoxDynamicStyle"
      @mousedown="onBoxMouseDown($event)"
  >
    <div class="resize-handle top-left" @mousedown="onCornerMouseDown($event, 'top-left')"></div>
    <div class="resize-handle top-right" @mousedown="onCornerMouseDown($event, 'top-right')"></div>
    <div class="resize-handle bottom-left" @mousedown="onCornerMouseDown($event, 'bottom-left')"></div>
    <div class="resize-handle bottom-right" @mousedown="onCornerMouseDown($event, 'bottom-right')"></div>
  </div>
</template>

<script setup lang="ts">

import {WritableComputedRef} from "@vue/reactivity";
import {ImageCropperRectangle, ImageData, Rectangle} from "~/utils/HarmonyTypes";
import {ComputedUtils} from "~/utils/ComputedUtils";
import {computed, PropType, reactive} from "vue";

const props = defineProps({
  modelValue: {
    type: Object as PropType<ImageCropperRectangle>,
    required: true
  },
  minimalWidth: {
    type: Number,
    default: 10
  },
  minimalHeight: {
    type: Number,
    default: 10
  },
  initialBoxValues: {
    type: Object as PropType<Rectangle>
  },
  imageData: {
    type: Object as PropType<ImageData>
  }
})

const emit = defineEmits<{
  (e: 'update:modelValue'): void
}>()

const computedModel:WritableComputedRef<ImageCropperRectangle> = ComputedUtils.createDelegatedComputedValue(props, getCurrentInstance()!)

const translateNaturalPxToDisplayPx = (naturalPx: number, naturalImageSize: number, displayImageSize: number):number => {
  return naturalPx * (displayImageSize / naturalImageSize)
}

const translateDisplayPxToNaturalPx = (displayPx: number, naturalImageSize: number, displayImageSize: number): number => {
  return displayPx * (naturalImageSize / displayImageSize)
}

const minimalDisplayWidth = computed(() => {
  const result = translateNaturalPxToDisplayPx(props.minimalWidth, props.imageData?.naturalWidth!, props.imageData?.displayWidth!)
  return result > 10 ? result : 10
});

const minimalDisplayHeight = computed(() => {
  const result = translateNaturalPxToDisplayPx(props.minimalHeight, props.imageData?.naturalHeight!, props.imageData?.displayHeight!)
  return result > 10 ? result : 10
});

const boxValues = reactive({
  x: props.initialBoxValues?.x!,  // Initial x position
  y: props.initialBoxValues?.y!,  // Initial y position
  width: props.initialBoxValues?.width!,   // Initial width of the box
  height: props.initialBoxValues?.height!,  // Initial height of the box
});

const updateModelValue = () => {
  computedModel.value.topLeftX = Math.floor(translateDisplayPxToNaturalPx(boxValues.x, props.imageData?.naturalWidth!, props.imageData?.displayWidth!))
  computedModel.value.topLeftY = Math.floor(translateDisplayPxToNaturalPx(boxValues.y, props.imageData?.naturalHeight!, props.imageData?.displayHeight!))
  computedModel.value.bottomRightX = computedModel.value.topLeftX + Math.floor(translateDisplayPxToNaturalPx(boxValues.width, props.imageData?.naturalWidth!, props.imageData?.displayWidth!))
  computedModel.value.bottomRightY = computedModel.value.topLeftY + Math.floor(translateDisplayPxToNaturalPx(boxValues.height, props.imageData?.naturalHeight!, props.imageData?.displayHeight!))
}

const cropperBoxDynamicStyle = computed(() => ({
  left: `${boxValues.x}px`,
  top: `${boxValues.y}px`,
  width: `${boxValues.width}px`,
  height: `${boxValues.height}px`,
}))

const mouseMoveValues = reactive({
  startClientX: undefined,
  startClientY: undefined,
  currentResizingCorner: "none"
});


const calculateNewMoveValue = (delta: number, currentBoxValue: number,  boxValueSize:  number, imageDisplayValueSize: number) => {
  const newValue = currentBoxValue + delta

  if(newValue < 0) {
    return 0;
  }

  if(newValue + boxValueSize > imageDisplayValueSize) {
    return imageDisplayValueSize - boxValueSize
  }

  return newValue
}

const calculateNewSizeValue = (delta: number, currentValue: number, minimalSize: number):number => {
  const newSize = currentValue + delta

  if(newSize > minimalSize) {
    return newSize
  } else {
    return minimalSize
  }
}

const onMouseMove = (event: any) => {
  event.preventDefault()

  const clientX = event.clientX
  const clientY = event.clientY

  const deltaX = clientX - mouseMoveValues.startClientX!
  const deltaY = clientY - mouseMoveValues.startClientY!

  if(mouseMoveValues.currentResizingCorner != "none") {

    if(mouseMoveValues.currentResizingCorner == "top-left") {

      const newWidth = calculateNewSizeValue(deltaX * (-1), boxValues.width, minimalDisplayWidth.value)
      if(newWidth > minimalDisplayWidth.value) {
        const newBoxX = boxValues.x + deltaX
        if(newBoxX > 0) {
          boxValues.width = newWidth
          boxValues.x = newBoxX
        }
      }

      const newHeight = calculateNewSizeValue(deltaY * (-1), boxValues.height, minimalDisplayHeight.value)
      if(newHeight > minimalDisplayHeight.value) {
        const newBoxY = boxValues.y + deltaY
        if(newBoxY > 0) {
          boxValues.height = newHeight
          boxValues.y = newBoxY
        }
      }

    }

    if(mouseMoveValues.currentResizingCorner == "top-right") {
      const newWidth = calculateNewSizeValue(deltaX, boxValues.width, minimalDisplayWidth.value)
      if(newWidth + boxValues.x < props.imageData?.displayWidth!) {
        boxValues.width = newWidth
      }

      const newHeight = calculateNewSizeValue(deltaY * (-1), boxValues.height, minimalDisplayHeight.value)
      if(newHeight > minimalDisplayHeight.value) {
        const newBoxY = boxValues.y + deltaY
        if(newBoxY > 0) {
          boxValues.height = newHeight
          boxValues.y = newBoxY
        }
      }

    }

    if(mouseMoveValues.currentResizingCorner == "bottom-left") {
      const newWidth = calculateNewSizeValue(deltaX * (-1), boxValues.width, minimalDisplayWidth.value)
      if(newWidth > minimalDisplayWidth.value) {
        const newBoxX = boxValues.x + deltaX
        if(newBoxX > 0) {
          boxValues.width = newWidth
          boxValues.x = newBoxX
        }
      }

      const newHeight = calculateNewSizeValue(deltaY, boxValues.height, minimalDisplayHeight.value)
      if(newHeight + boxValues.y < props.imageData?.displayHeight!) {
        boxValues.height = newHeight
      }
    }

    if(mouseMoveValues.currentResizingCorner == "bottom-right") {

      const newWidth = calculateNewSizeValue(deltaX, boxValues.width, minimalDisplayWidth.value)
      if(newWidth + boxValues.x < props.imageData?.displayWidth!) {
        boxValues.width = newWidth
      }

      const newHeight = calculateNewSizeValue(deltaY, boxValues.height, minimalDisplayHeight.value)
      if(newHeight + boxValues.y < props.imageData?.displayHeight!) {
        boxValues.height = newHeight
      }
    }


  } else {
    boxValues.x = calculateNewMoveValue(deltaX, boxValues.x, boxValues.width, props.imageData?.displayWidth!)
    boxValues.y = calculateNewMoveValue(deltaY, boxValues.y, boxValues.height, props.imageData?.displayHeight!)
  }


  mouseMoveValues.startClientX = event.clientX
  mouseMoveValues.startClientY = event.clientY


}

const onMouseUp = (event:any) => {
  event.preventDefault()

  mouseMoveValues.startClientX = undefined
  mouseMoveValues.startClientY = undefined
  mouseMoveValues.currentResizingCorner = "none"

  window.removeEventListener('mousemove', onMouseMove);
  window.removeEventListener('mouseup', onMouseUp);

  updateModelValue()
}

const onBoxMouseDown = (event:any) => {
  event.preventDefault()

  mouseMoveValues.startClientX = event.clientX
  mouseMoveValues.startClientY = event.clientY

  window.addEventListener('mousemove', onMouseMove);
  window.addEventListener('mouseup', onMouseUp);
}

const onCornerMouseDown = (event:any, corner: string) => {
  event.preventDefault()
  mouseMoveValues.currentResizingCorner = corner
}

</script>

<style scoped>

.cropper-box {
  position: absolute;
  border: 2px solid red;
  cursor: move;
}

.resize-handle {
  position: absolute;
  width: 10px;
  height: 10px;
  background-color: white;
  border: 2px solid red;
}

.resize-handle.top-left {
  top: -5px;
  left: -5px;
  cursor: nwse-resize;
}

.resize-handle.top-right {
  top: -5px;
  right: -5px;
  cursor: nesw-resize;
}

.resize-handle.bottom-left {
  bottom: -5px;
  left: -5px;
  cursor: nesw-resize;
}

.resize-handle.bottom-right {
  bottom: -5px;
  right: -5px;
  cursor: nwse-resize;
}

</style>