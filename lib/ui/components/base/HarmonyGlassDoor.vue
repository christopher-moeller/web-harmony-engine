<template>
  <div v-if="showBehindGlass" class="container">
    <div class="overlay"></div>
    <div class="content">
      <slot></slot>
    </div>
  </div>
  <template v-else>
    <slot></slot>
  </template>
</template>

<script setup>
import { defineProps } from 'vue';

// Define props for the component
const props = defineProps({
  showBehindGlass: {
    type: Boolean,
    default: true,
  },
});
</script>

<style scoped>
.container {
  width: 100%;
  position: relative;
  display: inline-block; /* Adjust as needed for your layout */
}

.overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  backdrop-filter: blur(1px) brightness(0.9);
  background-color: rgba(255, 255, 255, 0.6);
  pointer-events: none; /* Ensure overlay doesn't block interaction */
  border-radius: 8px; /* Optional: match slot content style */
  z-index: 2; /* Ensure the overlay is above the content */
}

.content {
  position: relative;
  z-index: 1; /* Ensure content is above the default level */
}

</style>
