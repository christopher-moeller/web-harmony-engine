<template>
    <div class="container">
        <LoadingSpinner />
    </div>
</template>
    
<script setup lang='ts'>
    
import LoadingSpinner from "@core/components/utils/LoadingSpinner.vue";
</script>
    
<style scoped>
.container {
  display: block;
  position: fixed;
  z-index: 2001;
  left: 0;
  top: 0;
  width: 100%;
  height: 100%;
  overflow: auto;
  background-color: rgb(0,0,0); /* Fallback color */
  background-color: rgba(0,0,0,0.4); /* Black w/ opacity */
}
</style>