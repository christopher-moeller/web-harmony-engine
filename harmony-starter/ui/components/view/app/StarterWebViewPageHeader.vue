<template>
  <header>
    <HarmonyViewLogo
        :is-in-app-mode="false"
        :name="name"
    />

  </header>
</template>

<script setup lang="ts">

import HarmonyViewLogo from "@core/components/view/HarmonyViewLogo.vue";
import useProjectMetaData from "@core/composables/useProjectMetaData";

const projectMetaData = useProjectMetaData()
const name = projectMetaData.projectLongName
</script>

<style scoped>

header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
}

</style>