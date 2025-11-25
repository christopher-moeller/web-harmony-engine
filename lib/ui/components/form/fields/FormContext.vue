<template>
  <slot></slot>
</template>

<script lang="ts" setup>

const props = defineProps({
  path: {
    type: String,
    required: true
  },
  arrayIndex: {
    type: Number,
    required: false
  }
})

</script>

<style scoped>

</style>