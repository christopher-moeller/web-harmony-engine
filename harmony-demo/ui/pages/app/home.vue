<template>
  <harmony-button caption="Open Modal" @click="isModalOpen = !isModalOpen" />
</template>

<script lang="ts" setup>
import HarmonyButton from "@core/components/base/HarmonyButton.vue"


const isModalOpen = ref(false)


</script>

<style scoped>

</style>