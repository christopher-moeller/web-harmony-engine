<template>
  <div class="split-pane" ref="splitPane">
    <div class="pane" :style="{ flex: state.leftPaneFlex }">
      <slot name="left"></slot>
    </div>
    <div
        class="divider"
        @mousedown="startDragging"
    ></div>
    <div class="pane" :style="{ flex: state.rightPaneFlex }">
      <slot name="right"></slot>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onBeforeUnmount } from 'vue';

const splitPane = ref(null);
const state = reactive({
  isDragging: false,
  initialPos: 0,
  leftPaneFlex: 0.5,
  rightPaneFlex: 0.5,
});

const startDragging = (event) => {
  state.isDragging = true;
  state.initialPos = event.clientX;

  document.addEventListener('mousemove', drag);
  document.addEventListener('mouseup', stopDragging);
};

const drag = (event) => {
  if (!state.isDragging) return;

  const currentPos = event.clientX;
  const delta = currentPos - state.initialPos;
  const paneWidth = splitPane.value.clientWidth;

  // Calculate new flex values based on delta movement
  const leftPaneWidth = paneWidth * state.leftPaneFlex + delta;
  const rightPaneWidth = paneWidth - leftPaneWidth;

  // Update flex values proportionally
  state.leftPaneFlex = leftPaneWidth / paneWidth;
  state.rightPaneFlex = rightPaneWidth / paneWidth;

  state.initialPos = currentPos;
};

const stopDragging = () => {
  state.isDragging = false;
  document.removeEventListener('mousemove', drag);
  document.removeEventListener('mouseup', stopDragging);
};

</script>

<style scoped>
.split-pane {
  display: flex;
  height: 100%;
  width: 100%;
  overflow: hidden;
  position: relative;
}

.pane {
  overflow: auto;
}

.divider {
  width: 5px;
  background: var(--harmony-light-3);
  cursor: ew-resize;
  position: relative;
  z-index: 10;
  border-radius: 5px;
}
</style>
